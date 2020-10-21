package RiotGamesDiscordBot.Commands;

import RiotGamesDiscordBot.RiotGamesAPI.Containers.RankedInfo;
import RiotGamesDiscordBot.RiotGamesAPI.Containers.SummonerInfo;
import RiotGamesDiscordBot.RiotGamesAPI.EmbeddedMessages.SummonerInfoEmbeddedMessageBuilder;
import RiotGamesDiscordBot.RiotGamesAPI.BracketGeneration.BracketManager;
import RiotGamesDiscordBot.RiotGamesAPI.RiotGamesAPI;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.IOException;
import java.util.ArrayList;

public class DiscordBotCommands extends ListenerAdapter {
    private static final String PREFIX = "~";
    private final Gson jsonManip;
    private BracketManager bracketManager;
    private final JDA discordAPI;

    public DiscordBotCommands(JDA discordAPI) {
        this.jsonManip = new Gson();
        this.discordAPI = discordAPI;
    }

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String[] message = event.getMessage().getContentRaw().split("\\s+");

        if (message[0].contains("~")) {
            String command = message[0].replace(PREFIX, "");
            switch(command) {
                case "test":
                    event.getChannel().sendTyping().queue();
                    event.getChannel().sendMessage("It is working!").queue();
                    break;
                case "lol":
                    if (message.length > 1) {
                        String arg2 = message[1];
                        switch(arg2) {
                            case "-si":
                                handleSummonerInfoRequest(event, message);
                                break;
                            case "-t":
                            handleTournamentRequest(event, message);
                            break;
                        }

                    }
                    else {
                        event.getChannel().sendMessage("Provide a summoner name to see information about them!").queue();
                    }
                    break;
                default:
                    event.getChannel().sendTyping().queue();
                    event.getChannel().sendMessage("Unrecognized command").queue();
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

    private void handleSummonerInfoRequest(GuildMessageReceivedEvent event, String[] message) {
        try {
            RiotGamesAPI riotGamesAPI = new RiotGamesAPI();
            StringBuilder summonerName = new StringBuilder();
            for (int i = 2; i < message.length; i++) {
                summonerName.append(message[i]);

                if (i + 1 == message.length) {
                    break;
                }

                summonerName.append("%20");
            }

            //Get Summoner Info
            SummonerInfo summonerInfo = this.jsonManip.fromJson(riotGamesAPI.getSummonerInfoByName(summonerName.toString()), SummonerInfo.class);

            //Get ranked info for previous Summoner
            ArrayList<RankedInfo> rankedInfo = this.jsonManip.fromJson(riotGamesAPI.getSummonerRankInfoByEncryptedSummonerID(summonerInfo.getEncryptedSummonerId()),
                    new TypeToken<ArrayList<RankedInfo>>(){}.getType());
            summonerInfo.setRankedInfo(rankedInfo);

            //Create the Embedded Message
            SummonerInfoEmbeddedMessageBuilder summonerInfoEmbeddedMessageBuilder = new SummonerInfoEmbeddedMessageBuilder(summonerInfo);

            event.getChannel().sendMessage(summonerInfoEmbeddedMessageBuilder.buildEmbeddedMessage()).queue();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleTournamentRequest(GuildMessageReceivedEvent event, String[] message) {
        if (message.length == 2) {
            event.getChannel().sendMessage("Please list the teams that will be participating.").queue();
            return;
        }
        try {
            ArrayList<String> teamNames = new ArrayList<>();
            StringBuilder teamName = new StringBuilder();
            for (int i = 2; i < message.length; i++) {
                String messageWord = message[i];

                if (messageWord.endsWith(";")) {
                    teamName.append(message[i].replace(";", ""));
                    teamNames.add(teamName.toString());
                    teamName.delete(0, teamName.length());
                } else {
                    teamName.append(message[i].replace(";", ""));
                    teamName.append(" ");
                }
            }

            this.bracketManager = new BracketManager(teamNames, this.discordAPI);
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
