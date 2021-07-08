package RiotGamesDiscordBot.RiotGamesAPI.Containers;

public class MatchMetaData {
    private final long tournamentId;
    private final String matchId;

    public MatchMetaData(long tournamentId, String matchId) {
        this.tournamentId = tournamentId;
        this.matchId = matchId;
    }

    public String getMatchId() {
        return matchId;
    }

    public long getTournamentId() {
        return tournamentId;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof MatchMetaData) {
            return (this.tournamentId == ((MatchMetaData) object).getTournamentId()) && (this.matchId.equals(((MatchMetaData) object).getMatchId()));
        }

        return false;
    }
}
