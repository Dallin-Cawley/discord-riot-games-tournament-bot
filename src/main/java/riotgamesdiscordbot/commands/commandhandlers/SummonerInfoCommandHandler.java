package riotgamesdiscordbot.commands.commandhandlers;

import riotgamesdiscordbot.riotgamesapi.containers.RankedInfo;
import riotgamesdiscordbot.riotgamesapi.containers.SummonerInfo;
import riotgamesdiscordbot.riotgamesapi.embeddedmessages.EmbeddedMessageBuilder;
import riotgamesdiscordbot.riotgamesapi.embeddedmessages.SummonerInfoEmbeddedMessageBuilder;
import riotgamesdiscordbot.riotgamesapi.RiotGamesAPI;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class SummonerInfoCommandHandler extends CommandHandler {
    private final String summonerName;
    private final RiotGamesAPI riotGamesAPI;

    public SummonerInfoCommandHandler(MessageReceivedEvent event, Iterator<String> messageIterator) {
        super(event, messageIterator);
        if (messageIterator.hasNext()) {
            this.summonerName = messageIterator.next();
        }
        else {
            this.summonerName = null;
        }
        this.riotGamesAPI = new RiotGamesAPI();
    }


    @Override
    public void handle() {
        Gson gson = new Gson();
        try {
            //Get Summoner Info
            SummonerInfo summonerInfo = gson.fromJson(riotGamesAPI.getSummonerInfoByName(summonerName), SummonerInfo.class);
            //Get ranked info for previous Summoner
            ArrayList<RankedInfo> rankedInfo = gson.fromJson(riotGamesAPI.getSummonerRankInfoByEncryptedSummonerID(summonerInfo.getEncryptedSummonerId()),
                    new TypeToken<ArrayList<RankedInfo>>() {}.getType());
            summonerInfo.setRankedInfo(rankedInfo);

            //Create the Embedded Message
            EmbeddedMessageBuilder summonerInfoEmbeddedMessageBuilder = new SummonerInfoEmbeddedMessageBuilder(summonerInfo);

            MessageCreateData data = MessageCreateData.fromEmbeds(summonerInfoEmbeddedMessageBuilder.buildMessageEmbed());
            event.getChannel().sendMessage(data).queue();
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
