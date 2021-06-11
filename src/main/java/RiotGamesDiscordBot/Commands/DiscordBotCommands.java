package RiotGamesDiscordBot.Commands;

import RiotGamesDiscordBot.Commands.CommandHandlers.CommandHandler;
import RiotGamesDiscordBot.Commands.CommandHandlers.SummonerInfoCommandHandler;
import RiotGamesDiscordBot.Commands.CommandHandlers.TournamentCommandHandler;
import RiotGamesDiscordBot.EventHandling.InputEventManager;
import RiotGamesDiscordBot.RiotGamesAPI.BracketGeneration.BracketManager;
import RiotGamesDiscordBot.Tournament.TournamentManager;
import com.google.gson.Gson;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Arrays;
import java.util.Iterator;

public class DiscordBotCommands extends ListenerAdapter {
    private final Gson gson;
    private BracketManager bracketManager;
    private final JDA discordAPI;
    private final InputEventManager inputEventManager;
    private final TournamentManager tournamentManager;

    public DiscordBotCommands(JDA discordAPI, TournamentManager tournamentManager) {
        this.gson = new Gson();
        this.discordAPI = discordAPI;
        this.tournamentManager = tournamentManager;
        this.inputEventManager = new InputEventManager(this.tournamentManager);
    }

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String[] message = event.getMessage().getContentRaw().split("\\s+");
        Iterator<String> messageIterator = Arrays.stream(message).iterator();
        String command = messageIterator.next();
        //League of Legends command
        if (command.equals("~lol")) {
            CommandHandler commandHandler;
            if (messageIterator.hasNext()) {
                String arg2 = messageIterator.next();
                switch (arg2) {
                    //Summoner Info
                    case "-si":
                        commandHandler = new SummonerInfoCommandHandler(event, messageIterator);
                        commandHandler.handle();
                        break;
                    //Tournament Start
                    case "-t":
                        if (messageIterator.hasNext()) {
                            if (messageIterator.next().equals("--rectify")) {
                                this.inputEventManager.handleEvent(messageIterator);
                                break;
                            }
                        }
                        else {
                            commandHandler = new TournamentCommandHandler(event, messageIterator, this.inputEventManager, this.tournamentManager, this.discordAPI);
                            commandHandler.handle();
                        }
                        break;
                    default:
                        commandHandler = new TournamentCommandHandler(event, messageIterator, this.inputEventManager, this.tournamentManager, this.discordAPI);
                        commandHandler.handle();

                }
            } else {
                event.getChannel().sendMessage("Please provide a command.").queue();
            }
        }
        //Any other message
        else {
            String channelName = event.getChannel().getName();

            //The tournament update channel
            if (channelName.equals("tournament-details")) {
                TextChannel channel = event.getChannel();

                //Expected to be the bracket image
                String recentMessageId = channel.getLatestMessageId();
                try {
                    Message recentMessage = channel.retrieveMessageById(recentMessageId).complete();
                    MessageType messageType = recentMessage.getType();

                    //Delete all non-pinned messages in channel
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
}
