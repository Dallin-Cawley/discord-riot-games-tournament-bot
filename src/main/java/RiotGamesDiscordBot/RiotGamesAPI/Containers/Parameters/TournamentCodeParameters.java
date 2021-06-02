package RiotGamesDiscordBot.RiotGamesAPI.Containers.Parameters;

import RiotGamesDiscordBot.RiotGamesAPI.Containers.MapType;
import RiotGamesDiscordBot.RiotGamesAPI.Containers.PickType;
import RiotGamesDiscordBot.RiotGamesAPI.Containers.SpectatorType;
import RiotGamesDiscordBot.Tournament.TournamentConfig;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class TournamentCodeParameters {
    private final Set<String> allowedSummonerIds;
    private final double teamSize;
    private final String metadata;
    private final PickType pickType;
    private final MapType mapType;
    private final SpectatorType spectatorType;

    public TournamentCodeParameters(List<String> allowedSummonerIds, TournamentConfig tournamentConfig) {
        this.teamSize = tournamentConfig.getTeamSize();
        this.pickType = tournamentConfig.getPickType();
        this.mapType = tournamentConfig.getMapType();
        this.spectatorType = tournamentConfig.getSpectatorType();
        this.metadata = tournamentConfig.getMetadata();

        this.allowedSummonerIds = new HashSet<>();
        this.allowedSummonerIds.addAll(allowedSummonerIds);
    }

    public double getTeamSize() {
        return teamSize;
    }
}
