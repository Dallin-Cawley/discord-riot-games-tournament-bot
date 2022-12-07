package riotgamesdiscordbot.tournament.roundrobin.events;

import riotgamesdiscordbot.eventhandling.ErrorEvent;
import riotgamesdiscordbot.riotgamesapi.containers.SummonerInfo;
import riotgamesdiscordbot.tournament.Team;
import riotgamesdiscordbot.tournament.Tournament;

public class TeamMemberDuplicateErrorEvent extends ErrorEvent {

    public TeamMemberDuplicateErrorEvent(SummonerInfo duplicateMember, Team team) {
        super("TeamMemberDuplicate Exception");
        this.message = duplicateMember.getSummonerName() + " seems to be on " + team.getTeamName() +
                " more than once.\n\n" + " Please ensure that each team does not have duplicate members in the Tournament Config file and then re-attempt tournament creation.";
    }

    @Override
    public void setup(Tournament tournament) {

    }
}
