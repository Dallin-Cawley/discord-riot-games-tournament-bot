package RiotGamesDiscordBot.RiotGamesAPI.Containers.MatchResult;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MatchResult {
    @SerializedName("startTime")
    long startTime;

    @SerializedName("winningTeam")
    List<SummonerName> winningTeam;

    @SerializedName("losingTeam")
    List<SummonerName> losingTeam;

    @SerializedName("shortCode")
    String shortCode;

    @SerializedName("metaData")
    MetaData metaData;

    @SerializedName("gameId")
    long gameId;

    @SerializedName("gameName")
    String gameName;

    @SerializedName("gameType")
    String gameType;

    @SerializedName("gameMap")
    int gameMap;

    @SerializedName("gameMode")
    String gameMode;

    @SerializedName("region")
    String region;


    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(this.metaData.title).append(" : ").append(this.region).append(" - ").append(this.gameMode);

        return stringBuilder.toString();
    }
}
