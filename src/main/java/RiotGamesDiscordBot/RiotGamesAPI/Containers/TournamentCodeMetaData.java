package RiotGamesDiscordBot.RiotGamesAPI.Containers;

public class TournamentCodeMetaData {
    private final long tournamentId;
    private final String matchId;

    public TournamentCodeMetaData(long tournamentId, String matchId) {
        this.tournamentId = tournamentId;
        this.matchId = matchId;
    }

    public String getMatchId() {
        return matchId;
    }

    public long getTournamentId() {
        return tournamentId;
    }
}
