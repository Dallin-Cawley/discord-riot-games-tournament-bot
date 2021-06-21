package RiotGamesDiscordBot.Tournament;

import RiotGamesDiscordBot.Logging.Level;
import RiotGamesDiscordBot.Logging.Logger;
import RiotGamesDiscordBot.RiotGamesAPI.Containers.MatchResult.MatchResult;
import RiotGamesDiscordBot.RiotGamesAPI.Containers.TournamentCodeMetaData;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class TournamentManager {

    private final List<Tournament> tournaments;

    public TournamentManager() {
        this.tournaments = new ArrayList<>();
    }

    public void registerTournament(Tournament tournament) {
        Logger.log("Registering Tournament : " + tournament.getTournamentId(), Level.INFO);
        this.tournaments.add(tournament);
        System.out.println("Tournaments size: " + tournaments.size());
    }

    public void removeTournament(Tournament removeTournament) {
        this.tournaments.removeIf(tournament -> tournament.equals(removeTournament));
        Logger.log("Successfully removed Tournament from list : " + removeTournament.getTournamentId(), Level.INFO);
    }

    public void advanceTournament(MatchResult matchResult) {
        TournamentCodeMetaData metaData = new Gson().fromJson(matchResult.getMetaData().getTitle(), TournamentCodeMetaData.class);
        System.out.println("Meta Data Tournament Id: " + metaData.getTournamentId());
        System.out.println("Meta Data Match ID: " + metaData.getMatchId());
        // Find Tournament the metaData belongs to and advance it
        System.out.println("Tournament Size: " + tournaments.size());
        for (Tournament tournament : this.tournaments) {
            System.out.println("\tTournament ID: " + tournament.getTournamentId());
            if (tournament.getTournamentId() == metaData.getTournamentId()) {
                tournament.advanceTournament(matchResult);
            }
        }

    }
}
