package riotgamesdiscordbot.eventhandling;

import riotgamesdiscordbot.tournament.Tournament;
import riotgamesdiscordbot.workerStep.WorkerStep;

import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * If a User has attempted to interact with a request from the Bot, resolve them.
 */
public class ResolveInteractions implements WorkerStep {

    private final Semaphore interactionsSemaphore;
    private final Map<String, Iterator<String>> interactions;
    private final Map<String, Event> events;
    private final Tournament tournament;
    private final List<String> removeInteractionIds;

    public ResolveInteractions(Semaphore interactionsSemaphore,
                               Map<String, Iterator<String>> interactions,
                               Map<String, Event> events,
                               Tournament tournament) {
        this.interactions = interactions;
        this.interactionsSemaphore = interactionsSemaphore;
        this.events = events;
        this.tournament = tournament;
        this.removeInteractionIds = new ArrayList<>();
    }

    /**
     * Any interaction with a registered {@link Event} is resolved and removed from the list.
     */
    @Override
    public void performStep() {
        Set<String> keys = this.events.keySet();

        try {
            this.interactionsSemaphore.acquire();
            for (String key : keys) {
                this.resolveInteraction(this.events.get(key));
            }
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }

        this.removeInteractions();

        this.interactionsSemaphore.release();
    }

    /**
     * Resolves the interaction if applicable
     * @param event The {@link Event} being resolved
     */
    private void resolveInteraction(Event event) {
        if (event instanceof Interactable) {
            if (this.interactions.get(event.getEventId()) != null) {
                TournamentResolvable resolvable = ((Interactable) event).handleInteraction(this.interactions.get(event.getEventId()));
                resolvable.resolve(this.tournament);
                this.removeInteractionIds.add(event.getEventId());
            }
        }
    }

    /**
     * Removes resolved interactions
     */
    private void removeInteractions() {
        for (String eventId : this.removeInteractionIds) {
            this.interactions.remove(eventId);
        }
    }
}
