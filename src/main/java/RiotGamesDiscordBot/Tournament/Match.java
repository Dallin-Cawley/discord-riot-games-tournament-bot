package RiotGamesDiscordBot.Tournament;

import java.util.List;

public class Match {
    private final Team teamOne;
    private final Team teamTwo;
    private final String tournamentCode;

    private Team winner;
    private Team loser;

    public Match(Team teamOne, Team teamTwo, String tournamentCode) {
        this.teamOne = teamOne;
        this.teamTwo = teamTwo;
        this.tournamentCode = tournamentCode;

        this.winner = null;
        this.loser = null;
    }

    public void addWinner(Team winner) {
        this.winner = winner;
    }

    public void addLoser(Team loser) {
        this.loser = loser;
    }

    public Team getTeamOne() {
        return teamOne;
    }

    public Team getTeamTwo() {
        return teamTwo;
    }

    public String getTournamentCode() {
        return tournamentCode;
    }

    public Team getLoser() {
        return loser;
    }

    public Team getWinner() {
        return winner;
    }
}
