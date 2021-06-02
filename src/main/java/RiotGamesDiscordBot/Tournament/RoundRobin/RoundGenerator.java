package RiotGamesDiscordBot.Tournament.RoundRobin;

import RiotGamesDiscordBot.RiotGamesAPI.Containers.Parameters.TournamentCodeParameters;
import RiotGamesDiscordBot.RiotGamesAPI.Containers.SummonerInfo;
import RiotGamesDiscordBot.RiotGamesAPI.RiotGamesAPI;
import RiotGamesDiscordBot.Tournament.Match;
import RiotGamesDiscordBot.Tournament.Team;
import RiotGamesDiscordBot.Tournament.TournamentConfig;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class RoundGenerator {
    private final List<Team> topRow;
    private final List<Team> bottomRow;
    private final long tournamentId;
    private final TournamentConfig tournamentConfig;

    public RoundGenerator(List<Team> teams, long tournamentId, TournamentConfig tournamentConfig) {
        this.topRow = new ArrayList<>();
        this.bottomRow = new ArrayList<>();
        this.tournamentId = tournamentId;
        this.tournamentConfig = tournamentConfig;

        int lastItem = teams.size() - 1;
        for (int i = 0; i < (teams.size() / 2); i++) {
            this.topRow.add(teams.get(i));
            this.bottomRow.add(teams.get(lastItem - i));
        }

    }

    public List<Match> generateMatches(RiotGamesAPI riotGamesAPI) {
        List<Match> matches = new ArrayList<>();

        //Iterate through each pair of teams to create a match
        for (int i = 0; i < this.topRow.size() && i < this.bottomRow.size(); i++) {
            Team teamOne = this.topRow.get(i);
            Team teamTwo = this.bottomRow.get(i);

            List<SummonerInfo> teamOneMembers = teamOne.getMembers();
            List<SummonerInfo> teamTwoMembers = teamTwo.getMembers();
            List<String> participantSummonerIds = new ArrayList<>();

            //Iterate through each member to get the Encrypted Summoner ID of each summoner for the tournament code
            System.out.println("\t" + teamOne.getTeamName() + " size: " + teamOne.getMembers().size());
            System.out.println("\t" + teamTwo.getTeamName() + " size: " + teamTwo.getMembers().size());
            for (int j = 0; j < teamOneMembers.size() && j < teamTwoMembers.size(); j++) {
                participantSummonerIds.add(teamOneMembers.get(j).getEncryptedSummonerId());
                participantSummonerIds.add(teamTwoMembers.get(j).getEncryptedSummonerId());
            }
            System.out.println("Participant Summoner IDs Size: " + participantSummonerIds.size());
            TournamentCodeParameters tournamentCodeParameters = new TournamentCodeParameters(participantSummonerIds, this.tournamentConfig);
            String tournamentCodeResponse = riotGamesAPI.getTournamentCodes(this.tournamentId, 1, tournamentCodeParameters);

            if (tournamentCodeResponse.contains("enoughPlayers") &&
                tournamentCodeResponse.contains("was false") &&
                participantSummonerIds.size() >= (tournamentCodeParameters.getTeamSize() * 2)) {
                System.out.println(participantSummonerIds);
                tournamentCodeResponse = riotGamesAPI.getTournamentCodes(this.tournamentId, 1, tournamentCodeParameters);
            }

            String[] tournamentCodes = new Gson().fromJson(tournamentCodeResponse, String[].class);

            Match match = new Match(this.topRow.get(i), this.bottomRow.get(i), tournamentCodes[0]);
            matches.add(match);
        }


        for (Match match : matches) {
            System.out.println("Match Tournament Code: " + match.getTournamentCode());
        }

        return matches;
    }

    /**
     * <pre>
     * Rotates the teams around the box counter-clockwise.
     *      1. The Team at the end of topRow is moved to the end of bottomRow.
     *      2. The Team at the beginning of bottomRow is moved to position 1 of topRow
     *
     *      -----------------
     *      | 1 | 2 | 3 | 4 |   topRow
     *      | 5 | 6 | 7 | 8 |   bottomRow
     *      -----------------
     *
     *      Team 4 would move to the position of Team 8
     *      Team 5 would move to the position of Team 2
     *
     * The movement of these teams would shift all teams one position left or right respectively.
     * </pre>
     */
    public void rotate() {
        //Remove teams from their positions.
        Team topTeam = this.topRow.remove(this.topRow.size() - 1);
        Team bottomTeam = this.bottomRow.remove(0);

        //Add teams to their final positions. topRow gets shifted to the right, bottomRow gets shifted to the left;
        this.topRow.add(1, bottomTeam);
        this.bottomRow.add(topTeam);
    }
}
