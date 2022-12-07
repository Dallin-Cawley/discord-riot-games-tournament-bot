package riotgamesdiscordbot.eventhandling;

import riotgamesdiscordbot.workerStep.WorkerStep;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Cleans up events that have expired
 */
public class RemoveExpiredEvents implements WorkerStep {

    private final Map<String, Event> events;
    private final Set<String> keys;

    public RemoveExpiredEvents(Map<String, Event> events) {
        this.events = events;
        this.keys = events.keySet();
    }

    @Override
    public void performStep() {
        List<String> eventsToRemove = new ArrayList<>();

        for (String key : this.keys) {
            Event event = this.events.get(key);

            if (event.expired()) {
                eventsToRemove.add(event.getEventId());
            }
        }

        this.remoteEvents(eventsToRemove);
    }

    private void remoteEvents(List<String> eventIds) {
        for (String eventId : eventIds) {
            this.events.remove(eventId);
        }
    }
}
