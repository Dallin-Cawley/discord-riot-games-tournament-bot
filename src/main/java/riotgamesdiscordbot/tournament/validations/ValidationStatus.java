package riotgamesdiscordbot.tournament.validations;


import java.util.HashMap;
import java.util.Map;

public class ValidationStatus {
    private final ValidationStatusCode statusCode;
    private final Map<String, Object> resources;
    private boolean removeTournament;

    public ValidationStatus(ValidationStatusCode statusCode) {
        this.resources = new HashMap<>();
        this.statusCode = statusCode;
        this.removeTournament = false;
    }

    public Object getResource(String key) {
        return this.resources.get(key);
    }

    public void addResource(String key, Object resource) {
        this.resources.put(key, resource);
    }

    public ValidationStatusCode getStatusCode() {
        return statusCode;
    }

    public void setRemoveTournament(boolean removeTournament) {
        this.removeTournament = removeTournament;
    }

    public boolean removeTournament() {
        return this.removeTournament;
    }
}
