package riotgamesdiscordbot.eventhandling;

import riotgamesdiscordbot.logging.discordlog.DiscordLogger;
import riotgamesdiscordbot.tournament.Tournament;
import riotgamesdiscordbot.workerStep.WorkerStep;

import java.util.*;
import java.util.concurrent.Semaphore;

public class EventManager extends Thread {
    private final Map<String, Event> events;
    private final Semaphore eventSemaphore;
    private boolean shutdown;
    private final Semaphore shutdownSemaphore;

    private final Map<String, Iterator<String>> interactions;
    private final Semaphore interactionsSemaphore;

    private final DiscordLogger discordLogger;
    private final Tournament tournament;
    private final List<WorkerStep> loopSteps;


    public EventManager(DiscordLogger discordLogger, Tournament tournament) {
        this.events = new HashMap<>();
        this.eventSemaphore = new Semaphore(1);

        this.shutdown = false;
        this.shutdownSemaphore = new Semaphore(1);

        this.interactions = new HashMap<>();
        this.interactionsSemaphore = new Semaphore(1);

        this.discordLogger = discordLogger;
        this.tournament = tournament;

        this.loopSteps = new ArrayList<>();
        this.loopSteps.add(new RemoveExpiredEvents(this.events));
        this.loopSteps.add(new ResolveInteractions(this.interactionsSemaphore,
                this.interactions, this.events, this.tournament));
        this.loopSteps.add(new SendMessageEvents(this.discordLogger, this.events));
    }


    @Override
    public void run() {
        try {
            shutdownSemaphore.acquire();

            while (!shutdown) {
                this.shutdownSemaphore.release();

                this.eventSemaphore.acquire();

                for (WorkerStep step : this.loopSteps) {
                    step.performStep();
                }

                this.eventSemaphore.release();

                // Sleep for 0.5 seconds before looking at the event list again
                Thread.sleep(500);

                shutdownSemaphore.acquire();
            }
        } catch (InterruptedException exception) {
            exception.printStackTrace();
            this.eventSemaphore.release();
            this.interactionsSemaphore.release();
            this.shutdownSemaphore.release();
        }
    }

    public DiscordLogger getDiscordLogger() {
        return this.discordLogger;
    }

    public void addEvent(Event event) {
        String eventId = UUID.randomUUID().toString();
        event.setEventId(eventId);
        event.setup(this.tournament);
        try {
            this.eventSemaphore.acquire();
            this.events.put(eventId, event);
            this.eventSemaphore.release();
        }
        catch (InterruptedException exception) {
            exception.printStackTrace();
            this.eventSemaphore.release();
        }
    }

    public void addInteraction(Iterator<String> message) {
        String eventId = message.next();
        try {
            this.interactionsSemaphore.acquire();
            this.interactions.put(eventId, message);
            this.interactionsSemaphore.release();
        }
        catch (InterruptedException exception) {
            exception.printStackTrace();
            this.interactionsSemaphore.release();
        }
    }

    public void shutDown() {
        try {
            shutdownSemaphore.acquire();
            shutdown = true;
            shutdownSemaphore.release();
        }
        catch (InterruptedException exception) {
            exception.printStackTrace();
            this.shutdownSemaphore.release();
        }
    }
}
