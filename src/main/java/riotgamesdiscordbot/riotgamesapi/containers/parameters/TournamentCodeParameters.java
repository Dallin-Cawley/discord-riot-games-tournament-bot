package riotgamesdiscordbot.riotgamesapi.containers.parameters;

import riotgamesdiscordbot.riotgamesapi.containers.MapType;
import riotgamesdiscordbot.riotgamesapi.containers.PickType;
import riotgamesdiscordbot.riotgamesapi.containers.SpectatorType;
import riotgamesdiscordbot.riotgamesapi.containers.MatchMetaData;
import riotgamesdiscordbot.tournament.TournamentConfig;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;


public class TournamentCodeParameters {
    private final List<String> allowedSummonerIds;
    private final double teamSize;
    private final String metadata;
    private final PickType pickType;
    private final MapType mapType;
    private final SpectatorType spectatorType;

    public TournamentCodeParameters(List<String> allowedSummonerIds, TournamentConfig tournamentConfig, MatchMetaData metaData) {
        this.teamSize = tournamentConfig.getTeamSize();
        this.pickType = tournamentConfig.getPickType();
        this.mapType = tournamentConfig.getMapType();
        this.spectatorType = tournamentConfig.getSpectatorType();
        this.metadata = new Gson().toJson(metaData);

        this.allowedSummonerIds = new ArrayList<>();
        this.allowedSummonerIds.addAll(allowedSummonerIds);
    }

    public double getTeamSize() {
        return teamSize;
    }
}
