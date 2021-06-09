package RiotGamesDiscordBot.Tournament.RoundRobin;

import RiotGamesDiscordBot.RiotGamesAPI.BracketGeneration.BracketManager;
import RiotGamesDiscordBot.RiotGamesAPI.Containers.MatchResult.MatchResult;
import RiotGamesDiscordBot.Tournament.Round;
import net.dv8tion.jda.api.JDA;

import java.io.IOException;
import java.util.List;

public class RoundRobinBracketManager extends BracketManager  {

    public RoundRobinBracketManager(JDA discordAPI) throws IOException {
        super(discordAPI);
    }

    @Override
    public void generateBracket(List<Round> rounds) {

    }

    @Override
    public void updateBracket(MatchResult matchResult) {

    }
}
