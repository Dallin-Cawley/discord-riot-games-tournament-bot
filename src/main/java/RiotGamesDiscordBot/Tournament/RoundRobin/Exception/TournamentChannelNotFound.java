package RiotGamesDiscordBot.Tournament.RoundRobin.Exception;

import RiotGamesDiscordBot.Tournament.RoundRobin.Events.TournamentChannelNotFoundEvent;
import net.dv8tion.jda.api.entities.TextChannel;

public class TournamentChannelNotFound extends Exception {
    public final TournamentChannelNotFoundEvent event;

    public TournamentChannelNotFound(TextChannel textChannel) {
        this.event = new TournamentChannelNotFoundEvent(textChannel);
    }
}
