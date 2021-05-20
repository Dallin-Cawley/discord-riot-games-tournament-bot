package RiotGamesDiscordBot.Commands;

import RiotGamesDiscordBot.Commands.CommandHandlers.CommandHandler;
import RiotGamesDiscordBot.Commands.CommandHandlers.SummonerInfoCommandHandler;
import RiotGamesDiscordBot.Commands.CommandHandlers.TournamentCommandHandler;
import RiotGamesDiscordBot.RiotGamesAPI.BracketGeneration.BracketManager;
import RiotGamesDiscordBot.RiotGamesAPI.RiotGamesAPI;
import com.google.gson.Gson;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class DiscordBotCommands extends ListenerAdapter {
    private final Gson gson;
    private BracketManager bracketManager;
    private final JDA discordAPI;

    public DiscordBotCommands(JDA discordAPI) {
        this.gson = new Gson();
        this.discordAPI = discordAPI;
    }

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String[] message = event.getMessage().getContentRaw().split("\\s+");
        Iterator<String> messageIterator = Arrays.stream(message).iterator();
        String command = messageIterator.next();
        if (command.equals("~lol")) {
            CommandHandler commandHandler;
            if (messageIterator.hasNext()) {
                String arg2 = messageIterator.next();
                switch (arg2) {
                    case "-si":
                        commandHandler = new SummonerInfoCommandHandler(event, messageIterator);
                        break;
                    case "-t":
                        commandHandler = new TournamentCommandHandler(event, messageIterator);
                        break;
                }

            } else {
                event.getChannel().sendMessage("Please provide a command.").queue();
            }
        }
        else {
            String channelName = event.getChannel().getName();
            if (channelName.equals("tournament-details")) {
                TextChannel channel = event.getChannel();
                String recentMessageId = channel.getLatestMessageId();
                try {
                    Message recentMessage = channel.retrieveMessageById(recentMessageId).complete();
                    MessageType messageType = recentMessage.getType();
                    if (!messageType.equals(MessageType.CHANNEL_PINNED_ADD)) {
                        for (Message pinnedMessage : channel.retrievePinnedMessages().complete()) {
                            channel.deleteMessageById(pinnedMessage.getId()).queue();
                        }

                        channel.pinMessageById(recentMessageId).queue();
                    }
                    else {
                        channel.deleteMessageById(recentMessageId).queue();
                    }
                }
                catch (NullPointerException exception) {
                    System.out.println("Encountered a NullPointerException");
                    exception.printStackTrace();
                }
            }
        }
    }

    private void handleTournamentRequest(GuildMessageReceivedEvent event, String[] message) throws MalformedURLException {
        RiotGamesAPI riotGamesAPI = new RiotGamesAPI();
        int providerID = riotGamesAPI.getProviderID(new URL("https://riot-api-tournaments.herokuapp.com/matchResult"), "NA");
        int tournamentID = riotGamesAPI.getTournamentID(providerID, "New Tournament");
    }
}
