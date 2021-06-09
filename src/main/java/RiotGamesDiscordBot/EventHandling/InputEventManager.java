package RiotGamesDiscordBot.EventHandling;

import RiotGamesDiscordBot.Logging.Level;
import RiotGamesDiscordBot.Logging.Logger;
import RiotGamesDiscordBot.Tournament.TournamentManager;

import java.util.*;
import java.util.concurrent.Semaphore;

public class InputEventManager {
    private final Map<String, UserHandleableEvent> events = new HashMap<>();
    private final Semaphore eventSemaphore = new Semaphore(1);
    private boolean shutdown = false;

    private final Thread eventHandling;
    private boolean eventHandlingStart;
    private final TournamentManager tournamentManager;

    /**
     * Initializes instance variables and sets up the thread.
     */
    public InputEventManager(TournamentManager tournamentManager) {
        this.eventHandlingStart = false;
        this.tournamentManager = tournamentManager;
        this.eventHandling = new Thread(new EventExpireRunnable(this.shutdown, this.eventSemaphore, this.events));
    }

    /**
     * Adds an event to the list waiting to be responded to. A message is sent to the channel that the event is occurring
     * in notifying those who can see of the ID of the event and how to rectify it. Does so in a thread safe manner.
     *
     * @param eventID String - the ID of the event
     * @param event HandleableEvent - The event to be added
     */
    public void registerEvent(String eventID, UserHandleableEvent event) {
        Logger.log("Attempting to acquire event handling semaphore to register event : " + eventID, Level.INFO);
        try {
            eventSemaphore.acquire();
        }
        catch (InterruptedException exception) {
            exception.printStackTrace();
        }
        Logger.log("Registering event : " + eventID, Level.INFO);
        events.put(eventID, event);

        Logger.log("Releasing event handling semaphore", Level.INFO);
        eventSemaphore.release();

        Logger.log("Sending event message to TextChannel", Level.INFO);
        event.sendMessage();
    }

    /**
     * Retrieves the event with the passed in eventID and calls it's HandleableEvent#handle() function. If the event
     * does not exist in the list, return false. Does so in a thread safe manner.
     *
     * @param args Iterator[String] - An iterator over the commands sent in the message
     */
    public void handleEvent(Iterator<String> args) {
        Logger.log("Attempting to acquire event handling semaphore", Level.INFO);
        try {
            eventSemaphore.acquire();
        }
        catch (InterruptedException exception) {
            exception.printStackTrace();
            return;
        }
        String eventID = args.next();
        Logger.log("Handling event: " + eventID, Level.INFO);
        UserHandleableEvent event = events.get(eventID);
        if (event != null) {
            event.handle(args);
            Logger.log("Successfully handled event : " + eventID, Level.INFO);
            Logger.log("Removing event : " + eventID + " : from event list", Level.INFO);
            events.remove(eventID);

            Logger.log("Releasing event handling semaphore", Level.INFO);
            eventSemaphore.release();

            event.resume();
            return;
        }
        Logger.log("Releasing event handling semaphore", Level.INFO);
        eventSemaphore.release();

    }

    /**
     * Removes the tournament from the registered tournaments list after sending the error message
     *
     * @param event UnHandleableTournamentEvent - The event that can't be handled by Bot Commands
     */
    public void handleUnHandleableEvents(UnHandleableTournamentEvent event) {
        event.sendErrorMessage();
        this.tournamentManager.removeTournament(event.getTournament());
    }

    /**
     * Begins the loop that checks if events have expired. If the thread has been started previously, nothing happens.
     */
    public void handleEvents() {
        if (!this.eventHandlingStart) {
            shutdown = false;
            this.eventHandling.start();
            this.eventHandlingStart = true;
        }
    }

    /**
     * Shuts down the loop that checks if events have expired and clears the event list.
     */
    public void shutdown() {
        shutdown = true;
        events.clear();
    }
}
