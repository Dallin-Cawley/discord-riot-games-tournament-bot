package riotgamesdiscordbot.eventhandling;

import java.util.Iterator;

public interface Interactable {

    /**
     * Handles the interaction between the User and the Event
     * @param args Iterator{@literal <}String{@literal >} - An Iterator to iterate through args from User.
     *
     * @return TournamentResolvable - An interface to revolve the event.
     */
    TournamentResolvable handleInteraction(Iterator<String> args);
}
