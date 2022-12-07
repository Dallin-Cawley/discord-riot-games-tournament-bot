package riotgamesdiscordbot.commands.commandhandlers;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Iterator;

public abstract class CommandHandler extends ListenerAdapter {
    Iterator<String> message;
    MessageReceivedEvent event;

    public CommandHandler(MessageReceivedEvent event, Iterator<String> messageIterator) {
        this.message = messageIterator;
        this.event = event;
    }

    public abstract void handle();
}
