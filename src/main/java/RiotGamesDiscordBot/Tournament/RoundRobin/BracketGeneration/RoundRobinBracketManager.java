package RiotGamesDiscordBot.Tournament.RoundRobin.BracketGeneration;

import RiotGamesDiscordBot.Logging.Level;
import RiotGamesDiscordBot.Logging.Logger;
import RiotGamesDiscordBot.RiotGamesAPI.BracketGeneration.BracketManager;
import RiotGamesDiscordBot.RiotGamesAPI.Containers.MatchResult.MatchResult;
import RiotGamesDiscordBot.Tournament.Round;
import RiotGamesDiscordBot.Tournament.RoundRobin.Exception.TournamentChannelNotFound;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RoundRobinBracketManager extends BracketManager  {
    private final TextChannel commandChannel;
    private final List<RoundImage> roundImages;

    public RoundRobinBracketManager(JDA discordAPI, TextChannel commandChannel) {
        super(discordAPI);
        this.commandChannel = commandChannel;
        this.roundImages = new ArrayList<>();
    }

    @Override
    public void generateBracket(List<Round> rounds) throws TournamentChannelNotFound {

        //Get the text channel the Tournament Bot will Post the current standings
        Logger.log("Attempting to find 'tournament-details' channel", Level.INFO);
        if (this.discordAPI.getTextChannels().size() <= 0) {
            Logger.log("There are no text channels.", Level.WARNING);
            throw new TournamentChannelNotFound(commandChannel);
        }
        else {
            List<TextChannel> textChannels = this.discordAPI.getTextChannels();
            for (TextChannel textChannel : textChannels) {
                if (textChannel.getName().equals("tournament-details")) {
                    Logger.log("Found 'tournament-details' TextChannel", Level.INFO);
                    this.tournamentChannel = textChannel;
                    break;
                }
            }

            // Tournament channel was not found.
            if (tournamentChannel == null) {
                Logger.log("Did not find 'tournament-details' TextChannel", Level.WARNING);
                throw new TournamentChannelNotFound(commandChannel);
            }
        }


        //TODO: Convert these calculations for the current standings
        // Round images will go out one round at a time

        // The amount of round images needed
        Logger.log("Rounds : " + rounds.size(), Level.INFO);
        int roundImageNum = rounds.size();

        Logger.log("Number of Round Images : " + roundImageNum, Level.INFO);

        //             Round image is 640px wide   Spacing between images
        int imageWidth = (roundImageNum * 640) + ((roundImageNum + 1) * 20);
        Logger.log("Image Width : " + imageWidth, Level.INFO);

        // Allow only 4 rounds per row
        int roundImageRows = 0;
        if (roundImageNum % 4 == 0) {
            roundImageRows = roundImageNum / 4;
        }
        else {
            roundImageRows = (roundImageNum / 4) + 1;
        }
        Logger.log("Rows : " + roundImageRows, Level.INFO);

        //              RoundImage is 640px high    Spacing between images
        int imageHeight = (roundImageRows * 640) + ((roundImageRows + 1) * 20);
        Logger.log("Image Height : " + imageHeight, Level.INFO);

        Logger.log("Creating master BufferedImage", Level.INFO);
        this.bracketImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_4BYTE_ABGR);
        this.bracketGraphics = this.bracketImage.createGraphics();

        for (Round round : rounds) {
            Logger.log("Creating RoundImage : " + round.getRoundNum(), Level.INFO);
            try {
                this.roundImages.add(new RoundImage(round));
            }
            catch(IOException exception) {
                Logger.log("Error generating RoundImage " + round.getRoundNum(), Level.ERROR);
                exception.printStackTrace();
            }
        }

        Logger.log("Finished Adding Rounds to master image", Level.INFO);

    }

    @Override
    public void updateBracket(MatchResult matchResult) {

    }

    public void sendRoundToChannel(int roundNum) {
        RoundImage round = this.roundImages.get(roundNum - 1);
        this.tournamentChannel.sendMessage("Round " + round.getRoundNum()).addFile(round.generateImage()).queue();
    }
}
