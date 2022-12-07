package riotgamesdiscordbot.riotgamesapi.containers.matchresult;

import com.google.gson.annotations.SerializedName;

public class MetaData {
    @SerializedName("title")
    public String title;

    public String getTitle() {
        return title;
    }
}
