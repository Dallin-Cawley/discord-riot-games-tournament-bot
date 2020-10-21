package RiotGamesDiscordBot.RiotGamesAPI.BracketGeneration;

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


public class BracketManager {
    private final BufferedImage bracketImage;
    private final Graphics2D bracketGraphics;

    private final JDA discordAPI;
    private TextChannel tournamentChannel;
    private final File imageFile;

    private final int imageHeight;
    private final int imageWidth;

    private final List<Match> matches;


    public BracketManager(ArrayList<String> teamNames, JDA discordAPI) throws IOException {
        this.matches = new ArrayList<>();
        this.discordAPI = discordAPI;
        this.imageFile = new File("bracketImage.png");

        //Get the text channel the Tournament Bot will Post the current standings
        for (TextChannel textChannel : this.discordAPI.getTextChannels()) {
            if (textChannel.getName().equals("tournament-details")) {
                this.tournamentChannel = textChannel;
                break;
            }
        }

        int rounds = (int)(Math.log(teamNames.size()) / Math.log(2));
        int matchNum = teamNames.size() / 2;

        //Create the Buffered Image
        this.imageHeight = (215 * (matchNum / 2)) + ((matchNum - 2) * 15);
        this.imageWidth = (rounds * 240) + 200;
        this.bracketImage = new BufferedImage(this.imageWidth,
                this.imageHeight, BufferedImage.TYPE_4BYTE_ABGR);
        System.out.println("Image Height: " + this.bracketImage.getHeight());
        System.out.println("Image Width: " + this.bracketImage.getWidth());

        //Create and set up the graphics
        this.bracketGraphics = this.bracketImage.createGraphics();
        this.bracketGraphics.setColor(Color.GRAY);
        this.bracketGraphics.fillRect(0, 0, this.imageWidth, this.imageHeight);
        this.bracketGraphics.setColor(Color.WHITE);
        this.bracketGraphics.setFont(new Font("Times New Roman", Font.BOLD, 24));

        //Generate the Tournament Bracket
        for (int round = 0; round < rounds; round++) {
            createMatches(matchNum, round);
            matchNum = matchNum / 2;
        }

        //Add in the initial Teams
        matchNum = teamNames.size() / 2;
        int team = 0;
        for (int match = 0; match < matchNum; match++, team += 2) {
            Match temp = this.matches.get(match);
            temp.setTeamOne(teamNames.get(team), this.bracketGraphics);
            temp.setTeamTwo(teamNames.get(team + 1), this.bracketGraphics);
        }

        sendBracketToChannel();

        int x = 0;
        int y = 0;

    }

    public void updateBrackets(Match match) throws IOException {
        //Redraw match image section in parent image
        this.bracketGraphics.drawImage(match.getMatchImage(), null, match.getPositionX(), match.getPositionY());
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

    private void createMatches(int matchNum, int round) {
        int imageHeightFragment = this.imageHeight / matchNum;
        int centerMatchOffset = (imageHeightFragment / 2) - 50;

        if (matchNum == 1) {
            Match match1 = new Match(this, (round * 240), centerMatchOffset);
            this.bracketGraphics.drawImage(match1.getMatchImage(), null, match1.getPositionX(), match1.getPositionY());

            int xPosition = (round * 240) + 200;
            this.bracketGraphics.drawRect(xPosition, match1.getPositionY() + 49, 25, 3);
            this.bracketGraphics.fillRect(xPosition, match1.getPositionY() + 49, 25, 3);
            return;
        }

        for (int match = 0; match < matchNum; match += 2) {

            //Create the 2 matches to be connected by the leaf
            Match match1 = new Match(this, (round * 240), (match * imageHeightFragment) + centerMatchOffset);
            this.bracketGraphics.drawImage(match1.getMatchImage(), null, match1.getPositionX(), match1.getPositionY());

            Match match2 = new Match (this, (round * 240), ((match + 1) * imageHeightFragment) + centerMatchOffset);
            this.bracketGraphics.drawImage(match2.getMatchImage(), null, match2.getPositionX(), match2.getPositionY());

            //Create the leaf
            int topArmPosition = match1.getPositionY() + 49;
            int bottomArmPosition = match2.getPositionY() + 49;
            int xPosition = (round * 240) + 200;
            new BracketLeaf(xPosition, topArmPosition, bottomArmPosition, this.bracketGraphics);

            this.matches.add(match1);
            this.matches.add(match2);

        }
    }


}
