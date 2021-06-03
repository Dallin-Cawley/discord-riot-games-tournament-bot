package RiotGamesDiscordBot.RiotGamesAPI.Containers.Parameters;

import RiotGamesDiscordBot.RiotGamesAPI.Containers.MapType;
import RiotGamesDiscordBot.RiotGamesAPI.Containers.PickType;
import RiotGamesDiscordBot.RiotGamesAPI.Containers.SpectatorType;
import RiotGamesDiscordBot.Tournament.TournamentConfig;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class TournamentCodeParameters {
    private final List<String> allowedSummonerIds;
    private final double teamSize;
    private final String metadata;
    private final PickType pickType;
    private final MapType mapType;
    private final SpectatorType spectatorType;

    public TournamentCodeParameters(List<String> allowedSummonerIds, TournamentConfig tournamentConfig) {
        System.out.println("Tournament Code Parameters list size: " + allowedSummonerIds.size());
        this.teamSize = tournamentConfig.getTeamSize();
        this.pickType = tournamentConfig.getPickType();
        this.mapType = tournamentConfig.getMapType();
        this.spectatorType = tournamentConfig.getSpectatorType();
        this.metadata = tournamentConfig.getMetadata();

        this.allowedSummonerIds = new ArrayList<>();
        this.allowedSummonerIds.addAll(allowedSummonerIds);
        System.out.println("Tournament Code Parameters size: " + this.allowedSummonerIds.size());
    }

    public double getTeamSize() {
        return teamSize;
    }
}
