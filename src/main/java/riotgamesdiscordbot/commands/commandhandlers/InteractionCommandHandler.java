package riotgamesdiscordbot.commands.commandhandlers;

import riotgamesdiscordbot.tournament.TournamentManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Iterator;

public class InteractionCommandHandler extends CommandHandler {


    public InteractionCommandHandler(MessageReceivedEvent event, Iterator<String> messageIterator) {
        super(event, messageIterator);
    }

    @Override
    public void handle() {
        //Retrieve tournament name from messageIterator
        String tournamentMetaData = this.message.next();
        System.out.println("Tournament meta data: " + tournamentMetaData);
        TournamentManager.getInstance().passInteraction(this.message, tournamentMetaData);
    }
}
