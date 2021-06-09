package RiotGamesDiscordBot.EventHandling;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;

public class EventExpireRunnable implements Runnable {
    private final Map<String, UserHandleableEvent> events;
    private boolean shutdown;
    private Semaphore eventSemaphore;

    public EventExpireRunnable(boolean shutdown, Semaphore eventSemaphore, Map<String, UserHandleableEvent> events) {
        this.shutdown = shutdown;
        this.eventSemaphore = eventSemaphore;
        this.events = events;
    }


    /**
     * Determine if any events have expired. If one has, call HandleableEvent#expire() method and remove it
     * from the list. Does so in a thread safe manner.
     */
    @Override
    public void run() {
        // Continue to check if any events have expired
        while (shutdown) {
            try {
                eventSemaphore.acquire();
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }

            Set<String> eventIds = events.keySet();
            for (String eventId : eventIds) {
                UserHandleableEvent event = events.get(eventId);
                if (event.isExpired()) {
                    event.expire();
                    events.remove(eventId);
                }
            }

            //Sleep for 5 seconds
            try {
                Thread.sleep(5000);
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }

        }
    }
}
