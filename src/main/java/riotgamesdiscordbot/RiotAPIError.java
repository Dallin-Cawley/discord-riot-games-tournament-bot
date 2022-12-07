package riotgamesdiscordbot;

import riotgamesdiscordbot.riotgamesapi.RiotAPIStatusLine;
import com.google.gson.annotations.SerializedName;

public class RiotAPIError {
    @SerializedName("status")
    private RiotAPIStatusLine statusLine;

    @Override
    public String toString() {
        return this.statusLine.toString();
    }

    public RiotAPIStatusLine getStatusLine() {
        return statusLine;
    }
}
