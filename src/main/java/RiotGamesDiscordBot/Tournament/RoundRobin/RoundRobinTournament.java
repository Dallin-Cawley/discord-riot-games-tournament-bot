package RiotGamesDiscordBot.Tournament.RoundRobin;

import RiotGamesDiscordBot.EventHandling.InputEventManager;
import RiotGamesDiscordBot.Logging.Level;
import RiotGamesDiscordBot.Logging.Logger;
import RiotGamesDiscordBot.RiotGamesAPI.Containers.MatchResult.MatchResult;
import RiotGamesDiscordBot.RiotGamesAPI.Containers.Parameters.TournamentCodeParameters;
import RiotGamesDiscordBot.RiotGamesAPI.Containers.SummonerInfo;
import RiotGamesDiscordBot.RiotGamesAPI.Containers.MatchMetaData;
import RiotGamesDiscordBot.RiotGamesAPI.EmbeddedMessages.TournamentWinnerEmbeddedMessageBuilder;
import RiotGamesDiscordBot.RiotGamesAPI.RiotGamesAPI;
import RiotGamesDiscordBot.Tournament.*;
import RiotGamesDiscordBot.Tournament.RoundRobin.BracketGeneration.RoundRobinBracketManager;
import RiotGamesDiscordBot.Tournament.RoundRobin.Events.DuplicateTeamEvent;
import RiotGamesDiscordBot.Tournament.RoundRobin.Events.MemberOnBothTeamsEvent;
import RiotGamesDiscordBot.Tournament.RoundRobin.Events.TeamMemberDuplicateEvent;
import RiotGamesDiscordBot.Tournament.RoundRobin.Exception.TournamentChannelNotFound;
import com.google.gson.Gson;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.List;

public class RoundRobinTournament extends Tournament implements Suspendable {

    private final TournamentConfig tournamentConfig;
    private boolean suspended;
    private Method resumeMethod;
    private Object resumeClassObject;
    private final InputEventManager eventManager;
    private final List<Round> rounds = new ArrayList<>();
    private final RoundRobinBracketManager bracketManager;
    private final JDA discordAPI;
    private int currentRound;
    private Team tournamentWinner;

    public RoundRobinTournament(int providerId, long tournamentId, TournamentConfig tournamentConfig, GuildMessageReceivedEvent event, List<Team> teams,
                                InputEventManager eventManager, JDA discordAPI) {
        super(tournamentId, providerId, event, teams);
        this.tournamentConfig = tournamentConfig;
        this.suspended = false;
        this.eventManager = eventManager;
        this.discordAPI = discordAPI;
        this.bracketManager = new RoundRobinBracketManager(this.discordAPI, event.getChannel(), this.teams);
        this.currentRound = 1;
    }

    public RoundRobinTournament(int providerId, long tournamentId, TournamentConfig tournamentConfig,
                                TextChannel textChannel, List<Team> teams, InputEventManager eventManager, JDA discordAPI) {
        super(tournamentId, providerId, textChannel, teams);
        this.tournamentConfig = tournamentConfig;
        this.suspended = false;
        this.eventManager = eventManager;
        this.discordAPI = discordAPI;
        this.bracketManager = new RoundRobinBracketManager(this.discordAPI, textChannel, this.teams);
        this.currentRound = 1;
    }

    /**
     * Validates the data surround the tournament. Validates that no team faces another twice, all members of a team
     * are unique, all participants of the tournament are unique, and all teams are unique.
     */
    @Override
    public void setup() {
        if (teams.size() % 2 == 1) {
            teams.add(new Team("BYE"));
        }

        // Ensure that all participants are unique. No participant is on multiple teams.
        Logger.log("Validating team compositions", Level.INFO);
        if (!this.validateTeamCompositions()) {
            Logger.log("Team composition failed validation", Level.WARNING);
            this.suspend();
            return;
        }

        // Generate the Rounds
        Logger.log("Generating Rounds", Level.INFO);
        int roundNum = this.teams.size() - 1;
        RoundGenerator roundGenerator = new RoundGenerator(this.teams, this.getTournamentId(), this.tournamentConfig);

        for (int i = 0; i < roundNum; i++) {
            Round round = roundGenerator.generateRound(i + 1);
            this.rounds.add(round);
            roundGenerator.rotate();
        }

        Logger.log("Validating rounds", Level.INFO);
        if (!this.validateRounds(this.rounds)) {
            Logger.log("Failed round validation. Suspending Tournament", Level.WARNING);
            this.suspend();
        }

        if (!this.suspended) {
            Logger.log("Passed all validation checks.", Level.INFO);
            this.start();
        }

    }

    @Override
    public void start() {
        Logger.log("Starting RoundRobin tournament", Level.INFO);

        // Generate Bracket before creating tournament codes

        try {
            this.bracketManager.generateBracket(this.rounds);
        }
        catch (TournamentChannelNotFound exception) {
            String uuid = UUID.randomUUID().toString();
            exception.event.setEventId(uuid);
            exception.event.setSuspendable(this);
            this.eventManager.registerEvent(uuid, exception.event);
            this.suspend();
            return;
        }

        // Create Tournament Codes
        RiotGamesAPI riotGamesAPI = new RiotGamesAPI();
        Gson gson = new Gson();
        Logger.log("Generating Tournament Codes", Level.INFO);

        for (Round round : rounds) {
            Logger.log("Generating tournament codes for Round " + round.getRoundNum(), Level.INFO);
            for (Match match : round) {
                if (match.getTeamOne().getTeamName().equals("BYE") || match.getTeamTwo().getTeamName().equals("BYE")) {
                    continue;
                }

                Logger.log("\tGenerating tournament code for " +
                        match.getTeamOne().getTeamName() + " vs " + match.getTeamTwo().getTeamName(), Level.INFO);
                // Used to create tournament code
                List<String> summonerIds = new ArrayList<>();
                for (Team team : match) {
                    for (SummonerInfo summonerInfo : team) {
                        summonerIds.add(summonerInfo.getEncryptedSummonerId());
                    }
                }

                TournamentCodeParameters parameters = new TournamentCodeParameters(summonerIds, this.tournamentConfig, match.getMetaData());
                try {
                    String[] tournamentCodes = gson.fromJson(riotGamesAPI.getTournamentCodes(this.getTournamentId(),
                            1, parameters), String[].class);
                    Logger.log("\tGenerated tournament code for " +
                            match.getTeamOne().getTeamName() + " vs " + match.getTeamTwo().getTeamName(), Level.INFO);
                    match.setTournamentCode(tournamentCodes[0]);
                }
                catch (IOException exception) {
                    exception.printStackTrace();
                }
            }

            Logger.log("Finished generating tournament codes for Round " + round.getRoundNum(), Level.INFO);
        }

        this.bracketManager.sendRoundToChannel(this.currentRound);
        this.bracketManager.sendCurrentStandings();
    }

    /**
     * <pre>
     * Validates that no participant is on two teams.
     *
     * If there are members on multiple teams, an instance of MemberOnBothTeamsEvent is registered that allows members of
     * the server to solve the issue. An event is registered on the first instance of a participant being on multiple teams.
     *
     * If there is a member that is on one team multiple times an instance of TeamMemberDuplicateEvent is registered.
     * This event is registered on the first encounter of a duplicate team member.
     * </pre>
     * @return true if all participants are unique, false otherwise
     */
    private boolean validateTeamCompositions() {
        // Ensure each member of each team is unique
        for (Team team : this.teams) {
            if (this.suspended) {
                break;
            }

            List<SummonerInfo> teamMembers = team.getMembers();

            // Check every team
            boolean passedTeam = false;
            for (Team diffTeam : this.teams) {
                if (this.suspended) {
                    break;
                }

                if (diffTeam.equals(team)) {
                    if (passedTeam) {
                        Logger.log(team.getTeamName() + " is listed twice in the Tournament Config file", Level.ERROR);
                        this.eventManager.handleUnHandleableEvents(new DuplicateTeamEvent(this, this.messageChannel, team));
                        return false;
                    }
                    passedTeam = true;
                    continue;
                }

                //make sure every Summoner of team is not in diffTeam
                for (SummonerInfo summoner : teamMembers) {
                    if (diffTeam.containsMember(summoner.getEncryptedSummonerId())) {
                        String eventID = UUID.randomUUID().toString();
                        MemberOnBothTeamsEvent memberOnBothTeamsEvent = new MemberOnBothTeamsEvent(this.messageChannel, eventID, summoner, team, diffTeam, this);
                        this.eventManager.registerEvent(eventID, memberOnBothTeamsEvent);
                        return false;
                    }
                }
            }

            //Make Sure that each team has unique members
            int indexOfDuplicate = this.validateTeam(team);
            if (indexOfDuplicate >= 0) {
                String eventID = UUID.randomUUID().toString();
                SummonerInfo duplicateMember = team.getMembers().get(indexOfDuplicate);
                TeamMemberDuplicateEvent teamMemberDuplicateEvent = new TeamMemberDuplicateEvent(this.messageChannel, duplicateMember, team, eventID, this);
                this.eventManager.registerEvent(eventID, teamMemberDuplicateEvent);
                return false;
            }
        }

        return true;
    }

    /**
     *
     * Make sure that each member of the team is unique. No repeat members
     *
     * @param team Team - the team that is being validated
     *
     * @return true if all members of the team are unique, false otherwise
     */
    private int validateTeam(Team team) {
        List<SummonerInfo> members = team.getMembers();

        for (int i = 0; i < members.size(); i++) {
            SummonerInfo member = members.get(i);

            for (int j = 0; j < members.size(); j++) {
                // Don't let member be compared with itself
                if (j == i) {
                    continue;
                }
                SummonerInfo anotherMember = members.get(j);
                // If there is a repeat, the team is not valid
                if (member.equals(anotherMember)) {
                    return j;
                }
            }
        }

        return -1;
    }

    /**
     * Ensure that no team faces each other twice. This will not create an event as an error would occur in the code,
     * not due to user error.
     *
     * @param rounds List[Round] - The created rounds
     *
     * @return true if rounds are valid, false otherwise
     */
    private boolean validateRounds(List<Round> rounds) {
        if (rounds.size() <= 0) {
            return false;
        }

        Round roundOne = rounds.get(0);

        for (Match match : roundOne) {
            Team teamOne = match.getTeamOne();
            Team teamTwo = match.getTeamTwo();

            if (this.duplicateOpponent(teamOne, rounds)) {
                // Register TeamDuplicateOpponent

                // Return false
                return false;
            }

            if (this.duplicateOpponent(teamTwo, rounds)) {
                // Register TeamDuplicateOpponent

                // Return false
                return false;
            }
        }

        return true;
    }

    /**
     *
     * Used to determine if the passed in team has any duplicate opponents. Every match in every round is iterated
     * through to make this determination.
     *
     * @param team Team - the team that needs determined if they have duplicate opponents
     * @param rounds List[Round] - the rounds that each team participates in.
     *
     * @return true if there is a duplicate opponent, false otherwise
     */
    private boolean duplicateOpponent(Team team, List<Round> rounds) {
        // KEY: teamName VALUE: times team is paired with opponent
        Map<String, Integer> opponentNum = new HashMap<>();

        for (Round round : rounds) {
            for (Match match : round) {
                // If the team in question is not part of the current match, continue
                if (!match.getTeamOne().equals(team) && !match.getTeamTwo().equals(team)) {
                    continue;
                }

                // Determine which team in the match is the team in question
                if (match.getTeamOne().equals(team)) {

                    // Increment the number of times the team has been paired with this opponent
                    String opponent = match.getTeamTwo().getTeamName();
                    if (!opponentNum.containsKey(opponent)) {
                        opponentNum.put(opponent, 1);
                    }
                    else {
                        int pairedWithNum = opponentNum.get(opponent);
                        pairedWithNum++;
                        opponentNum.put(opponent, pairedWithNum);
                    }
                }
                else if (match.getTeamTwo().equals(team)) {
                    String opponent = match.getTeamOne().getTeamName();

                    // Increment the number of times the team has been paired with this opponent
                    if (!opponentNum.containsKey(opponent)) {
                        opponentNum.put(opponent, 1);
                    }
                    else {
                        int pairedWithNum = opponentNum.get(opponent);
                        pairedWithNum++;
                        opponentNum.put(opponent, pairedWithNum);
                    }
                }
            }
        }

        // Determine if any opponent is paired with the team more than once
        Set<String> teamNames = opponentNum.keySet();
        for (String teamName : teamNames) {
            int pairedWithNum = opponentNum.get(teamName);

            if (pairedWithNum > 1) {
                Logger.log(teamName + " has been paired with " + team.getTeamName() + " " + pairedWithNum + " times", Level.WARNING);
                return true;
            }
        }
        return false;

    }

    private boolean isRoundOver() {
        for (Match match : this.rounds.get(this.currentRound - 1)) {
            if (!match.isDone()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void advanceTournament(MatchResult matchResult) {
        Round currentRound = this.rounds.get(this.currentRound - 1);
        MatchMetaData metaData = new Gson().fromJson(matchResult.getMetaData().getTitle(), MatchMetaData.class);

        for (Match match : currentRound) {
            if (metaData.getMatchId().equals(match.getMetaData().getMatchId())) {
                match.setMatchResult(matchResult);
                break;
            }
        }

        for (Team team : this.teams) {
            if (team.containsMember(matchResult.getWinningTeam().get(0))) {
                team.addWin();
            }
            if (team.containsMember(matchResult.getLosingTeam().get(0))) {
                team.addLoss();
            }
        }

        this.bracketManager.updateBracket(this.rounds.get(this.currentRound - 1));

        // Determine if the Round is over
        if (this.isRoundOver()) {
            this.currentRound++;

            // Tournament is Over
            System.out.println("0 indexed current round: " + (this.currentRound - 1));
            System.out.println("Round size: " + this.rounds.size());
            if ((this.currentRound - 1) == this.rounds.size()) {
                this.tournamentWinner = this.teams.get(0);

                for (Team team : this.teams) {
                    if (this.tournamentWinner.getWins() < team.getWins()) {
                        this.tournamentWinner = team;
                    }
                }
                this.endTournament();
            }
            // Tournament is ongoing
            else {
                this.bracketManager.sendRoundToChannel(this.currentRound);
            }
        }


    }

    @Override
    public void endTournament() {
        //TODO: Convert winning message to MessageEmbed
        this.messageChannel.sendMessage(new TournamentWinnerEmbeddedMessageBuilder(this.tournamentWinner).buildMessageEmbed()).queue();
        this.isDone = true;
    }

    @Override
    public void suspend() {
        this.suspended = true;
        StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();
        StackTraceElement currentMethod = stackTraces[2];

        try {
            Class<?> className = Class.forName(currentMethod.getClassName());
            this.resumeClassObject = className.getDeclaredConstructor(int.class, long.class, TournamentConfig.class,
                    TextChannel.class, List.class, InputEventManager.class, JDA.class)
                    .newInstance(this.getProviderId(), this.getTournamentId(), this.tournamentConfig,
                            this.messageChannel, this.teams, this.eventManager, this.discordAPI);
            this.resumeMethod = className.getMethod(currentMethod.getMethodName());
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void resume() {
        this.suspended = false;
        try {
            this.resumeMethod.invoke(this.resumeClassObject);
        }
        catch (IllegalAccessException | InvocationTargetException exception) {
            exception.printStackTrace();
        }
    }
}
