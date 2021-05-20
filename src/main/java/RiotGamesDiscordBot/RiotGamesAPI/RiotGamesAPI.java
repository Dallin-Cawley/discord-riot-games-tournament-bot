package RiotGamesDiscordBot.RiotGamesAPI;

import RiotGamesDiscordBot.Logging.Level;
import RiotGamesDiscordBot.Logging.Logger;
import RiotGamesDiscordBot.RiotAPIError;
import com.google.gson.Gson;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class RiotGamesAPI {
    private static final String BASE_URL = "https://na1.api.riotgames.com/";
    private static final String REGIONAL_BASE_URL = "https://americas.api.riotgames.com/";


    public int getProviderID(URL callBackURL, String region) {
        try {
            URI uri = new URI(REGIONAL_BASE_URL + "/lol/tournament-stub/v4/providers");
            HttpRequestContents requestContents = new HttpRequestContents(uri, RequestType.POST);
            requestContents.addRequestBodyAttribute("region", region);
            requestContents.addRequestBodyAttribute("url", callBackURL.toString());
            HttpResponseContents responseContents = request(requestContents);
            return Integer.parseInt(responseContents.toString());
        }
        catch (URISyntaxException exception) {
            exception.printStackTrace();
        }

        return -1;

    }

    public int getTournamentID(int providerID, String tournamentName) {
        try {
            URI uri = new URI(REGIONAL_BASE_URL + "/lol/tournament-stub/v4/tournaments");
            HttpRequestContents requestContents = new HttpRequestContents(uri, RequestType.POST);
            requestContents.addRequestBodyAttribute("name", tournamentName);
            requestContents.addRequestBodyAttribute("providerId", providerID);
            HttpResponseContents responseContents = request(requestContents);
            return Integer.parseInt(responseContents.toString());
        }
        catch (URISyntaxException exception) {
            exception.printStackTrace();
        }

        return -1;
    }

    public String getSummonerInfoByName(String summonerName) throws IOException {
        try {
            URI uri = new URI(BASE_URL + "/lol/summoner/v4/summoners/by-name/" + summonerName);
            HttpRequestContents requestContents = new HttpRequestContents(uri, RequestType.GET);
            HttpResponseContents responseContents = this.request(requestContents);
            return responseContents.toString();
        }
        catch (URISyntaxException exception) {
            exception.printStackTrace();
        }

        return null;
    }

    public String getSummonerRankInfoByEncryptedSummonerID(String encryptedSummonerId) throws IOException {
        try {
            URI uri = new URI(BASE_URL + "/lol/league/v4/entries/by-summoner/" + encryptedSummonerId);
            HttpRequestContents requestContents = new HttpRequestContents(uri, RequestType.GET);

            HttpResponseContents responseContents = request(requestContents);
            return responseContents.toString();
        }
        catch (URISyntaxException exception) {
            exception.printStackTrace();
        }

        return null;
    }



    private HttpResponseContents request(HttpRequestContents contents) {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpUriRequest request = getRequest(contents);
        request.addHeader("X-Riot-Token", System.getenv("RIOT_GAMES_API_KEY"));
        RiotAPIResponseHandler responseHandler = new RiotAPIResponseHandler();

        try {
            while(responseHandler.canAttempt()) {
                CloseableHttpResponse response = client.execute(request);
                responseHandler.handleResponse(response);
                responseHandler.handleEntity(response.getEntity());
            }

            if (!responseHandler.isSuccessful()) {
                RiotAPIError riotAPIError = new Gson().fromJson(new String(responseHandler.getResponseBytes(), StandardCharsets.UTF_8), RiotAPIError.class);
                Logger.log(riotAPIError.toString(), Level.WARNING);
            }
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }

        return new HttpResponseContents(responseHandler.getResponseBytes());
    }

    private HttpUriRequest getRequest(HttpRequestContents requestContents) {
        switch (requestContents.getRequestType()) {
            case GET:
                return new HttpGet(requestContents.getUri());
            case POST:
                HttpPost httpPost = new HttpPost(requestContents.getUri());
                try {
                    httpPost.setEntity(new StringEntity(requestContents.getRequestBody()));
                }
                catch(UnsupportedEncodingException exception) {
                    exception.printStackTrace();
                }
                return httpPost;
            case PATCH:
            default:
                return new HttpPatch(requestContents.getUri());
        }
    }
}
