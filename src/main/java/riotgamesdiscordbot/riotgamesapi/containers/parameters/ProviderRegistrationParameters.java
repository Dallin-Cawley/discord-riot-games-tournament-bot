package riotgamesdiscordbot.riotgamesapi.containers.parameters;

import riotgamesdiscordbot.riotgamesapi.containers.Region;

public class ProviderRegistrationParameters {
    private final String url;
    private final String region;

    public ProviderRegistrationParameters(Region region, String callbackURL) {
        this.url = callbackURL;
        this.region = region.name();
    }
}
