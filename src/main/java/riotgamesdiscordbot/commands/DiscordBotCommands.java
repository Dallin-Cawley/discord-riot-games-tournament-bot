package riotgamesdiscordbot.commands;

import riotgamesdiscordbot.commands.commandhandlers.CommandHandler;
import riotgamesdiscordbot.commands.commandhandlers.InteractionCommandHandler;
import riotgamesdiscordbot.commands.commandhandlers.SummonerInfoCommandHandler;
import riotgamesdiscordbot.commands.commandhandlers.TournamentCommandHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Arrays;
import java.util.Iterator;

public class DiscordBotCommands extends ListenerAdapter {
    private final JDA discordAPI;

    public DiscordBotCommands(JDA discordAPI) {
        this.discordAPI = discordAPI;
    }

    @Override
    public void onChannelCreate(ChannelCreateEvent event) {
        System.out.println("Channel create event");
        System.out.println("Channel type: " + event.getChannelType());
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String[] message = event.getMessage().getContentRaw().split("\\s+");
        System.out.println("Message: " + message);
        Iterator<String> messageIterator = Arrays.stream(message).iterator();
        String command = messageIterator.next();
        //League of Legends command
        if (command.equals("~lol")) {
            CommandHandler commandHandler;
            if (messageIterator.hasNext()) {
                String arg2 = messageIterator.next();
                switch (arg2) {
                    case "-si" -> commandHandler = new SummonerInfoCommandHandler(event, messageIterator);
                    case "-t" -> {
                        System.out.println("Creating Tournament");
                        commandHandler = new TournamentCommandHandler(event, messageIterator);
                    }
                    case "-i" -> {
                        System.out.println("Handling interaction");
                        commandHandler = new InteractionCommandHandler(event, messageIterator);
                    }
                    default -> commandHandler = new TournamentCommandHandler(event, messageIterator);
                }
                commandHandler.handle();
            } else {
                event.getChannel().sendMessage("Please provide a command.").queue();
            }
        }
    }
}

