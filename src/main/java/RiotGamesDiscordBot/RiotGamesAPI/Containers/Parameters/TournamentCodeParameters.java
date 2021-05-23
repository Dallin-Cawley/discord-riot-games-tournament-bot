package RiotGamesDiscordBot.RiotGamesAPI.Containers.Parameters;

import RiotGamesDiscordBot.RiotGamesAPI.Containers.MapType;
import RiotGamesDiscordBot.RiotGamesAPI.Containers.PickType;
import RiotGamesDiscordBot.RiotGamesAPI.Containers.SpectatorType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TournamentCodeParameters {
    private final Set<String> allowedSummonerIds;
    private final int teamSize;
    private final String metadata;
    private final PickType pickType;
    private final MapType mapType;
    private final SpectatorType spectatorType;

    public TournamentCodeParameters(List<String> allowedSummonerIds, int teamSize, PickType pickType, MapType mapType, SpectatorType spectatorType, String metadata) {
        this.teamSize = teamSize;
        this.pickType = pickType;
        this.mapType = mapType;
        this.spectatorType = spectatorType;
        this.metadata = metadata;

        this.allowedSummonerIds = new HashSet<>();
        this.allowedSummonerIds.addAll(allowedSummonerIds);
    }
}
