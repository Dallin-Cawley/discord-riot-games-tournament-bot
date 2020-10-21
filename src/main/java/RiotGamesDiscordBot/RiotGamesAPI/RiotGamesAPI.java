package RiotGamesDiscordBot.RiotGamesAPI;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class RiotGamesAPI {
    private static final String BASE_URL = "https://na1.api.riotgames.com/";
    private static final String API_KEY = "?api_key=" + System.getenv("RIOT_GAMES_API_KEY");

    private HttpsURLConnection connection;

    public RiotGamesAPI() {

    }

    public String getSummonerInfoByName(String summonerName) throws IOException {
        URL url = new URL(BASE_URL + "/lol/summoner/v4/summoners/by-name/" + summonerName + API_KEY);
        this.connection = (HttpsURLConnection) url.openConnection();

        return getResponse();
    }

    public String getSummonerRankInfoByEncryptedSummonerID(String encryptedSummonerId) throws IOException {
        URL url = new URL(BASE_URL + "/lol/league/v4/entries/by-summoner/" + encryptedSummonerId + API_KEY);
        this.connection = (HttpsURLConnection) url.openConnection();

        return getResponse();
    }

    private String getResponse() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(this.connection.getInputStream()));

        String line = "";
        StringBuilder builder = new StringBuilder();

        while (true) {
            line = reader.readLine();

            if (line == null) {
                break;
            }

            builder.append(line);

        }

        return builder.toString();
    }

}
