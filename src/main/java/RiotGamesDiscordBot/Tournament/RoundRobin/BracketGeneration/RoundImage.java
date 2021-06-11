package RiotGamesDiscordBot.Tournament.RoundRobin.BracketGeneration;

import RiotGamesDiscordBot.Tournament.Round;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class RoundImage {
    private final Round round;
    private final BufferedImage image;
    private final File imageFile;

    public RoundImage(Round round) throws IOException {
        this.round = round;
        this.image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("Round.png")));
        this.imageFile = new File("src/main/resources/roundImages/round" + round.getRoundNum() + ".png");
    }

    public File generateImage() {
        Graphics2D graphics = this.image.createGraphics();
        graphics.setFont(new Font( "SansSerif", Font.BOLD, 36));
        //TODO: Center Round String in Box
        graphics.drawString("Round " + this.round.getRoundNum(), 300, 200);

        try {
            this.writeFile();
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
        graphics.dispose();
        System.out.println("Image width: " + this.image.getWidth());
        System.out.println("Image height; " + this.image.getHeight());
        return this.imageFile;

    }

    /**
     * Draw a String centered in the middle of a Rectangle.
     *
     * @param g The Graphics instance.
     * @param text The String to draw.
     * @param rect The Rectangle to center the text in.
     */
    private void drawCenteredString(Graphics g, String text, Rectangle rect, Font font) {
        // Get the FontMetrics
        FontMetrics metrics = g.getFontMetrics(font);
        // Determine the X coordinate for the text
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        // Set the font
        g.setFont(font);
        // Draw the String
        g.drawString(text, x, y);
    }

    public int getRoundNum() {
        return this.round.getRoundNum();
    }

    private void writeFile() throws IOException {
        ImageIO.write(this.image, "png", this.imageFile);
    }
}
