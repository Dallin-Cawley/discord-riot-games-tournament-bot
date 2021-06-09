package RiotGamesDiscordBot.EventHandling;

import net.dv8tion.jda.api.entities.TextChannel;

import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.Map;

/**
 * Intended for events that can self manage when triggered.
 */
public interface UserHandleableEvent {
    /**
     * Handles the event
     */
    public void handle(Iterator<String> args);

    /**
     * Determines if the event has expired
     * @return boolean - true if it has expired, false otherwise
     */
    public boolean isExpired();

    /**
     * Retire the event and all resources attached to it. Reset state of objects or remove them from memory
     */
    public void expire();

    /**
     * Returns the identifier for a warning message.
     *
     * @return String - The identifier for a warning message
     */
    public String getWarning();

    /**
     * Sends a message to an user with instructions to rectify the event
     */
    public void sendMessage();


    /**
     * Resumes execution from the function that the event was created in
     */
    public void resume();
}
