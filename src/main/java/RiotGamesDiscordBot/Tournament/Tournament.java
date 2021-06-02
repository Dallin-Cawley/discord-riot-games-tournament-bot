package RiotGamesDiscordBot.Tournament;

import java.util.ArrayList;
import java.util.List;

public abstract class Tournament {
    private final long tournamentId;
    private final int  providerId;
    protected final List<Match> activeMatches;

    public Tournament(long tournamentId, int providerId) {
        this.tournamentId = tournamentId;
        this.providerId = providerId;
        this.activeMatches = new ArrayList<>();
    }

    public abstract void setup(List<Team> teams);

    public abstract void start();

    public abstract void updateStandings(Match match);

    public int getProviderId() {
        return providerId;
    }

    public long getTournamentId() {
        return tournamentId;
    }
}
