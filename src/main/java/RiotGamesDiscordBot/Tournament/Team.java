package RiotGamesDiscordBot.Tournament;

import RiotGamesDiscordBot.RiotGamesAPI.Containers.SummonerInfo;

import java.util.ArrayList;
import java.util.List;

public class Team {
    private final List<SummonerInfo> members;
    private  final String teamName;

    private int wins;
    private int losses;

    public Team(String teamName) {
        this.members = new ArrayList<>();
        this.teamName = teamName;

        this.wins = 0;
        this.losses = 0;
    }

    public String getWinLossRatio() {
        return wins + ":" + losses;
    }

    public void addWin() {
        this.wins++;
    }

    public void addLoss() {
        this.losses++;
    }

    public void addMember(SummonerInfo member) {
        this.members.add(member);
    }

    public int getLosses() {
        return losses;
    }

    public int getWins() {
        return wins;
    }

    public List<SummonerInfo> getMembers() {
        return members;
    }

    public String getTeamName() {
        return teamName;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append(this.teamName).append("\n");
        for (SummonerInfo member : this.members) {
            builder.append("\t").append(member.getSummonerName()).append("\n");
        }

        return builder.toString();
    }
}
