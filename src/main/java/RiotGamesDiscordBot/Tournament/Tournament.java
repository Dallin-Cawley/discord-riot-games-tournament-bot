package RiotGamesDiscordBot.Tournament;

import java.util.ArrayList;
import java.util.List;

public abstract class Tournament {
    private final String tournamentId;
    private final List<Match> activeMatches;

    public Tournament(String tournamentId) {
        this.tournamentId = tournamentId;
        this.activeMatches = new ArrayList<>();
    }

    public abstract void setup();

    public abstract void start();

    public abstract void updateStandings(Match match);
}
