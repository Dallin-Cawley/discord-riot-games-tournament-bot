package riotgamesdiscordbot.tournament.roundrobin.events;

import riotgamesdiscordbot.eventhandling.ErrorEvent;
import riotgamesdiscordbot.tournament.roundrobin.events.containers.MemberOnMultipleTeamsContainer;
import riotgamesdiscordbot.tournament.Team;
import riotgamesdiscordbot.tournament.Tournament;


public class MemberOnMultipleTeamsEvent extends ErrorEvent {
    private final MemberOnMultipleTeamsContainer container;

    public MemberOnMultipleTeamsEvent(MemberOnMultipleTeamsContainer container) {
        super("MemberOnMultipleTeams Exception");
        this.container = container;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.container.summonerInfo.getSummonerName()).append(" seems to be on multiple teams.").append("\n\n");

        for (Team team: this.container.teams) {
            stringBuilder.append("\t").append(team.getTeamName()).append("\n");
        }
        stringBuilder.append("Please ensure that every summoner is included in the Tournament Config file once.\n");
        stringBuilder.append("You may reattempt tournament creation after this has been solved. Thank you.");

        this.message = stringBuilder.toString();
    }

    @Override
    public void setup(Tournament tournament) {

    }
}
