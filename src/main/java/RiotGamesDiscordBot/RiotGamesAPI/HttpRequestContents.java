package RiotGamesDiscordBot.RiotGamesAPI;

import com.google.gson.Gson;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestContents {
    private final URI uri;
    private final RequestType requestType;
    private final Map<String, Object> requestBody;

    public HttpRequestContents(URI uri, RequestType requestType) {
        this.uri = uri;
        this.requestType = requestType;
        this.requestBody = new HashMap<>();
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public URI getUri() {
        return uri;
    }

    public void addRequestBodyAttribute(String key, Object value) {
        requestBody.put(key, value);
    }

    public String getRequestBody() {
        String jsonString = new Gson().toJson(this.requestBody);
        System.out.println(jsonString);
        return jsonString;
    }

}
