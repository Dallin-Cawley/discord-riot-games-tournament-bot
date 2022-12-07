package riotgamesdiscordbot.tournament.events;

import riotgamesdiscordbot.eventhandling.ErrorEvent;
import riotgamesdiscordbot.tournament.Tournament;

public class InsufficientParticipantsEvent extends ErrorEvent {

    public InsufficientParticipantsEvent() {
        super("InsufficientParticipants Exception");
        this.message = "There must be at least 20 participants for me to create a Tournament. Please find more summoners to join the tournament and add them to the tournament config file before trying again.";

    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void setup(Tournament tournament) {
        tournament.setCannotContinue(true);
    }
}
