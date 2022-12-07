package riotgamesdiscordbot.eventhandling;

import riotgamesdiscordbot.tournament.Tournament;

public class EventMock extends Event {
    public EventMock(String eventTitle) {
        super(eventTitle);
    }

    @Override
    public void setup(Tournament tournament) {
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }


}
