package RiotGamesDiscordBot.Tournament;

import RiotGamesDiscordBot.Logging.Level;
import RiotGamesDiscordBot.Logging.Logger;

import java.util.ArrayList;
import java.util.List;

public class TournamentManager {
    private final List<Tournament> tournaments = new ArrayList<>();

    public void registerTournament(Tournament tournament) {
        Logger.log("Registering Tournament : " + tournament.getTournamentId(), Level.INFO);
        this.tournaments.add(tournament);
    }

    public void removeTournament(Tournament removeTournament) {
        this.tournaments.removeIf(tournament -> tournament.equals(removeTournament));
        Logger.log("Successfully removed Tournament from list : " + removeTournament.getTournamentId(), Level.INFO);
    }
}
