package riotgamesdiscordbot.riotgamesapi.containers;

import com.google.gson.annotations.SerializedName;

public class MiniSeries {
    @SerializedName("losses")
    int losses;

    @SerializedName("progress")
    String progress;

    @SerializedName("target")
    int target;

    @SerializedName("wins")
    String wins;
}
