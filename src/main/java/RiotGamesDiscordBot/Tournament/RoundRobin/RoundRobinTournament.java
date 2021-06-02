package RiotGamesDiscordBot.Tournament.RoundRobin;

import RiotGamesDiscordBot.RiotGamesAPI.RiotGamesAPI;
import RiotGamesDiscordBot.Tournament.Match;
import RiotGamesDiscordBot.Tournament.Team;
import RiotGamesDiscordBot.Tournament.Tournament;
import RiotGamesDiscordBot.Tournament.TournamentConfig;

import java.util.ArrayList;
import java.util.List;

public class RoundRobinTournament extends Tournament {

    private final TournamentConfig tournamentConfig;

    public RoundRobinTournament(int providerId, long tournamentId, TournamentConfig tournamentConfig) {
        super(tournamentId, providerId);
        this.tournamentConfig = tournamentConfig;
    }

    @Override
    public void setup(List<Team> teams) {
        if (teams.size() % 2 == 1) {
            teams.add(new Team("BYE"));
        }

        RoundGenerator roundGenerator = new RoundGenerator(teams, this.getTournamentId(), tournamentConfig);
        int roundNum = teams.size() - 1;
        System.out.println("Round Num: " + roundNum);
        List<List<Match>> rounds = new ArrayList<>();

        RiotGamesAPI riotGamesAPI = new RiotGamesAPI();
        for (int i = 0; i < roundNum; i++) {
            System.out.println("Round : " + i);
            List<Match> matches = roundGenerator.generateMatches(riotGamesAPI);
            rounds.add(matches);
            roundGenerator.rotate();
        }

        System.out.println(rounds);


    }

    @Override
    public void start() {

    }

    @Override
    public void updateStandings(Match match) {

    }
}
