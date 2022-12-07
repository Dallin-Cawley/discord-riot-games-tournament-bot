package riotgamesdiscordbot.tournament.roundrobin.events.containers;

import riotgamesdiscordbot.riotgamesapi.containers.SummonerInfo;
import riotgamesdiscordbot.tournament.Team;

import java.util.ArrayList;
import java.util.List;

public class MemberOnMultipleTeamsContainer {

    public List<Team> teams;
    public SummonerInfo summonerInfo;

    public MemberOnMultipleTeamsContainer(SummonerInfo summonerInfo) {
        this.summonerInfo = summonerInfo;
        this.teams = new ArrayList<>();
    }

    private boolean isSummonerInfo(SummonerInfo summonerInfo) {
        return this.summonerInfo.equals(summonerInfo);
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof SummonerInfo summoner) {
            return this.isSummonerInfo(summoner);
        }
        else if (object instanceof MemberOnMultipleTeamsContainer) {
            return ((MemberOnMultipleTeamsContainer) object).summonerInfo.equals(this.summonerInfo);
        }

        else return false;
    }
}
