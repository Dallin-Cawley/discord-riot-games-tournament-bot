package riotgamesdiscordbot.tournament.roundrobin.events;

import riotgamesdiscordbot.eventhandling.ErrorEvent;
import riotgamesdiscordbot.tournament.Team;
import riotgamesdiscordbot.tournament.Tournament;

public class DuplicateTeamEvent extends ErrorEvent {

    public DuplicateTeamEvent(Team duplicateTeam) {
        super("DuplicateTeam Exception");
        this.message = duplicateTeam.getTeamName() + " " +
                "is listed in the Tournament Configuration excel file multiple times.\n\n" +
                "There is no Bot Command that can rectify this event. Because of this, the tournament will be removed. " +
                "Please remove or replace the duplicate team and try again.";
    }

    @Override
    public void setup(Tournament tournament) {

    }
}
