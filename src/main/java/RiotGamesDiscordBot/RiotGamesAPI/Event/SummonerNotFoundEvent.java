package RiotGamesDiscordBot.RiotGamesAPI.Event;

import RiotGamesDiscordBot.EventHandling.UnHandleableEvent;
import net.dv8tion.jda.api.entities.TextChannel;

public class SummonerNotFoundEvent implements UnHandleableEvent {

    private final TextChannel textChannel;
    private final String summonerName;

    public SummonerNotFoundEvent(TextChannel textChannel, String summonerName) {
        this.textChannel = textChannel;
        this.summonerName = summonerName;
    }

    @Override
    public void sendErrorMessage() {
        String stringBuilder = this.summonerName + " was not found. Common reasons for this can include a " +
                "misspelling or this summoner is not on the NA region.\n" +
                "Please correct the spelling or replace them in the tournament configuration file and restart " +
                "the tournament.";
        textChannel.sendMessage(stringBuilder).queue();
    }
}
