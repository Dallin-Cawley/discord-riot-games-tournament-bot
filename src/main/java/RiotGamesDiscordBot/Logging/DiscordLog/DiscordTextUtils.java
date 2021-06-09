package RiotGamesDiscordBot.Logging.DiscordLog;

public class DiscordTextUtils {

    public static String colorRed(String message) {

        return "```diff" + "\n" + '-' +
                message + '\n' +
                "```";
    }
}
