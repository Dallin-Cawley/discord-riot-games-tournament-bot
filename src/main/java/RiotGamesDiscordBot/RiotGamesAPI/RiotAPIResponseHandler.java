package RiotGamesDiscordBot.RiotGamesAPI;

import RiotGamesDiscordBot.Logging.Level;
import RiotGamesDiscordBot.Logging.Logger;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.ByteBuffer;

public class RiotAPIResponseHandler {
    private boolean canAttempt;
    private boolean successful;
    private byte[] responseBytes;

    public RiotAPIResponseHandler() {
        this.canAttempt = true;
        this.successful = false;
    }

    public boolean canAttempt() {
        return this.canAttempt;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public byte[] getResponseBytes() {
        return responseBytes;
    }

    public void handleResponse(CloseableHttpResponse response) {
        int statusCode = response.getStatusLine().getStatusCode();
        switch (statusCode) {
            case 400: //Bad Request
            case 401: //Unauthorized
                canAttempt = false;
                successful = false;
                break;
            case 403: //Forbidden
                System.out.println("Forbidden. Did you refresh the API token?");
                break;
            case 404: //Not Found
            case 415: //Unsupported Media Type
            case 429: //Rate Limit Exceeded
                this.handle429(response);
                break;
            case 500: //Internal Server Error
            case 503: //Service Unavailable
            default:
                canAttempt = false;
                successful = true;
        }
    }

    public void handleEntity(HttpEntity entity) throws IOException {
        this.responseBytes = entity.getContent().readAllBytes();
        EntityUtils.consumeQuietly(entity);
    }

    private void handle429(CloseableHttpResponse response) {
        Header[] headers = response.getHeaders("Retry-After");
        int retryAfter = Integer.parseInt(headers[0].getElements()[0].getValue());
        try {
            Thread.sleep(retryAfter * 1000L);
        }
        catch (InterruptedException exception) {
            Logger.log("Encountered InterruptedException in response to 429 Error Code", Level.WARNING);
        }

        this.canAttempt = true;
        this.successful = false;
    }

}
