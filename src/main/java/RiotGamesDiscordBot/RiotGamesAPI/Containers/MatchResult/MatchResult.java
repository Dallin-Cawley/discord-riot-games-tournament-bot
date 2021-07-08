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

    public MetaData getMetaData() {
        return metaData;
    }

    @Override
    public String toString() {
        return this.metaData.title + " : " + this.region + " - " + this.gameMode;
    }

    public List<SummonerName> getLosingTeam() {
        return losingTeam;
    }

    public List<SummonerName> getWinningTeam() {
        return winningTeam;
    }
}
