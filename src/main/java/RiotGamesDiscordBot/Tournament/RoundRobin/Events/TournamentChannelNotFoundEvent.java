package RiotGamesDiscordBot.Tournament.RoundRobin.Events;

import RiotGamesDiscordBot.EventHandling.UserHandleableEvent;
import RiotGamesDiscordBot.Tournament.Suspendable;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Iterator;

public class TournamentChannelNotFoundEvent implements UserHandleableEvent {
    private final TextChannel textChannel;
    private Suspendable suspendable;
    private String eventId;

    public TournamentChannelNotFoundEvent(TextChannel textChannel) {
        this.textChannel = textChannel;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void setSuspendable(Suspendable suspendable) {
        this.suspendable = suspendable;
    }

    @Override
    public void handle(Iterator<String> args) {
        // Nothing to do but try again
    }

    @Override
    public boolean isExpired() {
        return false;
    }

    @Override
    public void expire() {

    }

    @Override
    public String getWarning() {
        return null;
    }

    @Override
    public void sendMessage() {

        String message = "A text channel with the name 'tournament-details' was not found Please create one" +
                " as this is the channel that the updated brackets will be sent to. If this text channel" +
                " does not exist, the bot cannot display the bracket.\n" +
                "Please use the following command to try again after 'tournament-details' is available...\n\n\t" +
                " ~lol -t --rectify [ EVENT ID ] -tde\n\n" +
                "Event ID : " + this.eventId;

        this.textChannel.sendMessage(message).queue();
    }

    @Override
    public void resume() {
        this.suspendable.resume();
    }
}
