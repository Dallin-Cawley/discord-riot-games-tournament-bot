package RiotGamesDiscordBot;

import RiotGamesDiscordBot.RiotGamesAPI.RiotAPIStatusLine;
import com.google.gson.annotations.SerializedName;

public class RiotAPIError {
    @SerializedName("status")
    RiotAPIStatusLine statusLine;

    @Override
    public String toString() {
        return this.statusLine.toString();
    }
}
