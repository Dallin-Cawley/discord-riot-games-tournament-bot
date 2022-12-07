package riotgamesdiscordbot.tournament;

import riotgamesdiscordbot.logging.Level;
import riotgamesdiscordbot.logging.Logger;
import riotgamesdiscordbot.riotgamesapi.containers.matchresult.MatchResult;
import riotgamesdiscordbot.riotgamesapi.containers.MatchMetaData;
import riotgamesdiscordbot.riotgamesapi.containers.Region;
import riotgamesdiscordbot.riotgamesapi.containers.SummonerInfo;
import riotgamesdiscordbot.riotgamesapi.RiotGamesAPI;
import riotgamesdiscordbot.tournament.events.InsufficientParticipantsEvent;
import riotgamesdiscordbot.tournament.roundrobin.events.containers.MemberOnMultipleTeamsContainer;
import riotgamesdiscordbot.tournament.roundrobin.events.MemberOnMultipleTeamsEvent;
import riotgamesdiscordbot.tournament.roundrobin.events.TeamMemberDuplicateErrorEvent;
import riotgamesdiscordbot.tournament.validations.ValidationStatus;
import riotgamesdiscordbot.tournament.validations.ValidationStatusCode;
import riotgamesdiscordbot.tournament.validator.ValidatorFactory;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Semaphore;

public class TournamentManager extends Thread{

    private final Semaphore                     activeTournamentsSemaphore;
    private final List<Tournament>              activeTournaments;

    private final Semaphore                     registeredTournamentsSemaphore;
    private final List<Long>                    registeredTournaments;

    private final Semaphore                     idleTournamentsSemaphore;
    private final List<Tournament>              idleTournaments;

    private final Semaphore                     tournamentsAwaitingStartSemaphore;
    private final List<Tournament>              tournamentsAwaitingStart;

    private final Semaphore                     shutdownSemaphore;
    private       boolean                       shutdown;

    private final Semaphore                     interactionSemaphore;
    private final Map<String, Iterator<String>> interactions;

    private final Semaphore                     advanceTournamentSemaphore;
    private final List<MatchResult>             matchResults;

    private static TournamentManager Instance;

    private TournamentManager() {
        this.activeTournamentsSemaphore = new Semaphore(1);
        this.activeTournaments = new ArrayList<>();

        this.registeredTournamentsSemaphore = new Semaphore(1);
        this.registeredTournaments = new ArrayList<>();

        this.tournamentsAwaitingStartSemaphore = new Semaphore(1);
        this.tournamentsAwaitingStart = new ArrayList<>();

        this.idleTournamentsSemaphore = new Semaphore(1);
        this.idleTournaments = new ArrayList<>();

        this.shutdownSemaphore = new Semaphore(1);
        this.shutdown = false;

        this.interactionSemaphore = new Semaphore(1);
        this.interactions = new HashMap<>();

        this.advanceTournamentSemaphore = new Semaphore(1);
        this.matchResults = new ArrayList<>();
    }

    public static TournamentManager getInstance() {
        if (Instance == null) {
            Instance = new TournamentManager();
            Instance.start();
        }

        return Instance;
    }

    @Override
    public void run() {
        try {
            this.shutdownSemaphore.acquire();
            while (!this.shutdown) {
                this.shutdownSemaphore.release();
                List<Tournament> deleteTournaments = new ArrayList<>();
                /* ***********************************************************************************************
                 * Check if any tournaments have resumed since last iteration
                 *************************************************************************************************/
                this.idleTournamentsSemaphore.acquire();
                List<Tournament> removeTournament = new ArrayList<>();
                for (Tournament tournament : this.idleTournaments) {
                    if (!tournament.isIdle()) {
                        // Check if the tournament is set up
                        removeTournament.add(tournament);
                        if (!tournament.isSetup()) {
                            this.awaitStart(tournament);
                        }
                        else {
                            Logger.log("95 - Activating tournament: " + tournament.getTournamentId(), Level.ERROR);
                            this.isActive(tournament);
                        }
                    }
                }

                for (Tournament tournament: removeTournament) {
                    this.idleTournaments.remove(tournament);
                }
                this.idleTournamentsSemaphore.release();


                /* ***********************************************************************************************
                 * Progress Any tournaments that need it
                 *************************************************************************************************/
                this.tournamentsAwaitingStartSemaphore.acquire();
                removeTournament.clear();
                for (Tournament tournament : this.tournamentsAwaitingStart) {
                    if (!tournament.isSetup()) {
                        System.out.println("Setting up tournament");
                        tournament.setup();
                    }

                    // Check if the tournament is registered. For a tournament to be registered it must be set up.
                    if (!this.activeTournaments.contains(tournament) && tournament.isSetup()) {
                        System.out.println("Registering tournament");
                        ValidationStatus status = this.registerTournament(tournament);
                        Logger.log("Registration status: " + status.getStatusCode(), Level.INFO);
                        if (status.getStatusCode() != ValidationStatusCode.OK) {
                            this.handleRegisterErrorStatus(status, tournament);

                            if (status.removeTournament()) {
                                deleteTournaments.add(tournament);
                            }

                        }
                    }

                    // Check if the tournament is started. In order for a tournament to start it must be set up, and it must be registered
                    if (!tournament.isStarted() && this.registeredTournaments.contains(tournament.getTournamentId()) && tournament.isSetup()) {
                        System.out.println("Starting tournament");
                        tournament.start();
                        removeTournament.add(tournament);
                        this.isActive(tournament);
                    }
                }

                for (Tournament tournament : removeTournament) {
                    this.tournamentsAwaitingStart.remove(tournament);
                }

                this.tournamentsAwaitingStartSemaphore.release();

                /* **********************************************************************************************
                 * Check if any interactions need to be passed to a tournament
                 ************************************************************************************************/
                this.interactionSemaphore.acquire();

                Set<String> keySet = this.interactions.keySet();
                for (String key : keySet) {
                    Iterator<String> messageIterator = this.interactions.get(key);
                    for (Tournament tournament : this.idleTournaments) {
                        if (key.toLowerCase(Locale.ROOT).equals(tournament.getTournamentConfig().getMetadata().toLowerCase(Locale.ROOT))) {
                            tournament.passInteraction(messageIterator);
                            this.interactions.remove(key);
                            break;
                        }
                    }
                }

                this.interactionSemaphore.release();

                /* **********************************************************************************************
                 * Check if any tournaments need to be advanced
                 ************************************************************************************************/
                this.advanceTournamentSemaphore.acquire();
                List<MatchResult> removeMatchResultsFrom = new ArrayList<>();
                for (MatchResult matchResult : this.matchResults) {
                    MatchMetaData metaData = matchResult.getMetaData();
                    Logger.log(metaData.toString(), Level.INFO);
                    try {
                        // Find Tournament the metaData belongs to and advance it
                        this.activeTournamentsSemaphore.acquire();
                        Logger.log("Tournament list size: " + this.activeTournaments.size(), Level.INFO);
                        for (Tournament tournament : this.activeTournaments) {
                            Logger.log("Tournament in active : " + tournament.getTournamentConfig().getMetadata(), Level.INFO);
                            if (tournament.getTournamentId() == metaData.getTournamentId()) {
                                // Advance the tournament
                                tournament.advanceTournament(matchResult);

                                // If tournament advancement was successful, add to the list to be removed later.
                                removeMatchResultsFrom.add(matchResult);
                            }

                            if (tournament.isDone()) {
                                deleteTournaments.add(tournament);
                            }
                        }

                        this.activeTournamentsSemaphore.release();
                    } catch (InterruptedException exception) {
                        exception.printStackTrace();
                    }
                }

                // Remove the used match results from the list
                for (MatchResult matchResult : removeMatchResultsFrom) {
                    this.matchResults.remove(matchResult);
                }

                this.advanceTournamentSemaphore.release();

                /* **********************************************************************************************
                 * Check if any tournaments cannot continue
                 ************************************************************************************************/
                this.activeTournamentsSemaphore.acquire();
                for (Tournament tournament : this.activeTournaments) {
                    if (tournament.cannotContinue()) {
                        deleteTournaments.add(tournament);
                    }
                }
                this.activeTournamentsSemaphore.release();

                this.idleTournamentsSemaphore.acquire();
                for (Tournament tournament : this.idleTournaments) {
                    if (tournament.cannotContinue()) {
                        deleteTournaments.add(tournament);
                    }
                }
                this.idleTournamentsSemaphore.release();

                this.tournamentsAwaitingStartSemaphore.acquire();
                for (Tournament tournament : this.tournamentsAwaitingStart) {
                    if (tournament.cannotContinue()) {
                        deleteTournaments.add(tournament);
                    }
                }
                this.tournamentsAwaitingStartSemaphore.release();

                /* **********************************************************************************************
                 * Remove tournaments if needed
                 ************************************************************************************************/
                this.activeTournamentsSemaphore.acquire();
                this.idleTournamentsSemaphore.acquire();
                this.tournamentsAwaitingStartSemaphore.acquire();
                for (Tournament tournament : deleteTournaments) {
                    Logger.log("Removing tournament : " + tournament.getTournamentConfig().getMetadata(), Level.WARNING);
                    this.activeTournaments.remove(tournament);
                    this.idleTournaments.remove(tournament);
                    this.tournamentsAwaitingStart.remove(tournament);
                }
                this.activeTournamentsSemaphore.release();
                this.idleTournamentsSemaphore.release();
                this.tournamentsAwaitingStartSemaphore.release();

                Thread.sleep(500);
                this.shutdownSemaphore.acquire();
            }
        }
        catch (InterruptedException exception) {
            exception.printStackTrace();
        }
    }

    public void shutdown() {
        try {
            this.shutdownSemaphore.acquire();
            this.shutdown = true;
            this.shutdownSemaphore.release();


            //Shut down all tournament event manager threads
            this.activeTournamentsSemaphore.acquire();
            for (Tournament tournament : this.activeTournaments) {
                tournament.shutdownEventManager();
            }
            this.activeTournamentsSemaphore.release();

        }
        catch (InterruptedException exception) {
            exception.printStackTrace();
        }
    }

    public ValidationStatus registerTournament(Tournament tournament) {
        ValidationStatus status = this.validateTournament(tournament);

        if (status.getStatusCode() == ValidationStatusCode.OK) {
            // Create a Tournament ID for the tournament after passing
            RiotGamesAPI riotGamesAPI = new RiotGamesAPI();
            try {
                int providerID = riotGamesAPI.getProviderID(new URL("https://discord-lol-tournent-bot.herokuapp.com/matchResult/"), Region.NA);
                long tournamentId = riotGamesAPI.getTournamentID(providerID, "New Tournament");
                tournament.setProviderId(providerID);
                tournament.setTournamentId(tournamentId);
                Logger.log("Tournament ID: " + tournament.getTournamentId(), Level.INFO);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                // Register Tournament
                this.registeredTournamentsSemaphore.acquire();
                this.registeredTournaments.add(tournament.getTournamentId());
                this.registeredTournamentsSemaphore.release();
            }
            catch (InterruptedException exception) {
                exception.printStackTrace();
            }

        }

        return status;
    }

    private void handleRegisterErrorStatus(ValidationStatus status, Tournament tournament) {
        switch (status.getStatusCode()) {
            case DUPLICATE_MEMBERS_ON_TEAM -> {
                TeamMemberDuplicateErrorEvent teamMemberDuplicateErrorEvent = new TeamMemberDuplicateErrorEvent((SummonerInfo) status.getResource("duplicateMember"),
                        (Team) status.getResource("team"));
                tournament.eventManager.addEvent(teamMemberDuplicateErrorEvent);
            }
            case DUPLICATE_PARTICIPANTS_IN_TOURNAMENT -> {
                MemberOnMultipleTeamsEvent memberOnMultipleTeamsEvent = new MemberOnMultipleTeamsEvent((MemberOnMultipleTeamsContainer) status.getResource("MemberOnMultipleTeamsContainer"));
                tournament.eventManager.addEvent(memberOnMultipleTeamsEvent);
            }
            case NOT_ENOUGH_PARTICIPANTS -> {
                InsufficientParticipantsEvent insufficientParticipantsEvent = new InsufficientParticipantsEvent();
                tournament.eventManager.addEvent(insufficientParticipantsEvent);
            }
            case NOT_SETUP -> {
            }
        }
    }

    public Tournament getTournament(long tournamentId) {
        try {
            if (!this.registeredTournaments.contains(tournamentId)) {
                return null;
            }

            this.activeTournamentsSemaphore.acquire();
            for (Tournament tournament : this.activeTournaments) {
                if (tournament.getTournamentId() == tournamentId) {
                    this.activeTournamentsSemaphore.release();
                    return tournament;
                }
            }
            this.activeTournamentsSemaphore.release();


            this.idleTournamentsSemaphore.acquire();
            for (Tournament tournament : this.idleTournaments) {
                if (tournament.getTournamentId() == tournamentId) {
                    this.idleTournamentsSemaphore.release();
                    return tournament;
                }
            }
            this.idleTournamentsSemaphore.release();


            this.tournamentsAwaitingStartSemaphore.acquire();
            for (Tournament tournament : this.tournamentsAwaitingStart) {
                if (tournament.getTournamentId() == tournamentId) {
                    this.tournamentsAwaitingStartSemaphore.release();
                    return tournament;
                }
            }
            this.tournamentsAwaitingStartSemaphore.release();
        }
        catch (InterruptedException exception) {
            exception.printStackTrace();
        }

        return null;
    }

    public void idleTournament(Tournament tournament) {
        tournament.setIdle(true);
        try {
            this.idleTournamentsSemaphore.acquire();
            this.idleTournaments.add(tournament);
            this.idleTournamentsSemaphore.release();
        }
        catch (InterruptedException exception) {
            exception.printStackTrace();
        }
    }

    public void awaitStart(Tournament tournament) {
        try {
            this.tournamentsAwaitingStartSemaphore.acquire();
            this.tournamentsAwaitingStart.add(tournament);
            this.tournamentsAwaitingStartSemaphore.release();
        }
        catch (InterruptedException exception) {
            exception.printStackTrace();
        }
    }

    public void isActive(Tournament tournament) {
        tournament.isStarted = true;
        try {
            this.activeTournamentsSemaphore.acquire();
            this.activeTournaments.add(tournament);
            this.activeTournamentsSemaphore.release();
        }
        catch (InterruptedException exception) {
            exception.printStackTrace();
        }
    }

    public void advanceTournament(MatchResult matchResult) {
        try {
            this.advanceTournamentSemaphore.acquire();
            this.matchResults.add(matchResult);
            this.advanceTournamentSemaphore.release();
        }
        catch (InterruptedException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Validates a given tournament with the appropriate {@link riotgamesdiscordbot.tournament.validator.TournamentValidator}
     * @param tournament Tournament - the tournament to validate.
     * @return RegisterTournamentStatus - An object containing the status of the registration and helpful information about the error.
     */
    private ValidationStatus validateTournament(Tournament tournament) {
        return ValidatorFactory.getValidator(tournament).validate();
    }

    public void passInteraction(Iterator<String> messageIterator, String tournamentMetaData) {
        try {
            this.interactionSemaphore.acquire();
            this.interactions.put(tournamentMetaData, messageIterator);
            this.interactionSemaphore.release();
        }
        catch (InterruptedException exception) {
            exception.printStackTrace();
        }
    }
}
