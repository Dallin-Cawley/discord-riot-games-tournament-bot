package RiotGamesDiscordBot.RiotGamesAPI.Containers.MatchResult;

import com.google.gson.annotations.SerializedName;

public class MetaData {
    @SerializedName("title")
    String title;

    public String getTitle() {
        return title;
    }
}
