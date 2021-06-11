package RiotGamesDiscordBot.RiotGamesAPI.BracketGeneration;

import RiotGamesDiscordBot.RiotGamesAPI.Containers.MatchResult.MatchResult;
import RiotGamesDiscordBot.Tournament.Match;
import RiotGamesDiscordBot.Tournament.Round;
import RiotGamesDiscordBot.Tournament.RoundRobin.Exception.TournamentChannelNotFound;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public abstract class BracketManager {
    protected BufferedImage bracketImage;
    protected Graphics2D bracketGraphics;

    protected final JDA discordAPI;
    protected TextChannel tournamentChannel;
    protected final File imageFile;

    protected final List<MatchImage> matchImages;


    public BracketManager(JDA discordAPI) {
        this.matchImages = new ArrayList<>();
        this.discordAPI = discordAPI;
        this.imageFile = new File("/resources/bracketImage.png");
    }

    public abstract void generateBracket(List<Round> rounds) throws TournamentChannelNotFound;

    public abstract void updateBracket(MatchResult matchResult);

    public void updateBrackets(MatchImage matchImage) throws IOException {
        //Redraw match image section in parent image
        this.bracketGraphics.drawImage(matchImage.getMatchImage(), null, matchImage.getPositionX(), matchImage.getPositionY());
        sendBracketToChannel();
    }

    public void sendBracketToChannel() throws IOException {
        //Delete all the pinned messages
        List<Message> pinnedMessages = this.tournamentChannel.retrievePinnedMessages().complete();
        if (!pinnedMessages.isEmpty()) {
            for (Message message : pinnedMessages) {
                this.tournamentChannel.deleteMessageById(message.getId()).queue();
            }
        }

        //Update parent image
        writeFile();

        //Send the Bracket image
        this.tournamentChannel.sendMessage("Current Standings.").addFile(this.imageFile).queue();
    }

    private void writeFile() throws IOException {
        ImageIO.write(this.bracketImage, "png", this.imageFile);
    }


}
