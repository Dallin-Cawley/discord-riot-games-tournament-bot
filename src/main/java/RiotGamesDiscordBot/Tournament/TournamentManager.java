package RiotGamesDiscordBot.Tournament;

import RiotGamesDiscordBot.Logging.Level;
import RiotGamesDiscordBot.Logging.Logger;
import RiotGamesDiscordBot.RiotGamesAPI.Containers.MatchResult.MatchResult;
import RiotGamesDiscordBot.RiotGamesAPI.Containers.MatchMetaData;
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
        MatchMetaData metaData = new Gson().fromJson(matchResult.getMetaData().getTitle(), MatchMetaData.class);

        // Find Tournament the metaData belongs to and advance it
        for (Tournament tournament : this.tournaments) {
            if (tournament.getTournamentId() == metaData.getTournamentId()) {
                tournament.advanceTournament(matchResult);
            }
        }

        this.tournaments.removeIf(Tournament::isDone);
    }
}
