package RiotGamesDiscordBot.EventHandling;

/**
 * An event has occurred that cannot be rectified via Bot commands.
 */
public interface UnHandleableEvent {
    /**
     * Sends the error message informing the User that the event is not recoverable. Inform the user any way
     * that they can rectify the issue outside of bot commands.
     */
    public void sendErrorMessage();
}
