package RiotGamesDiscordBot.Tournament.SingleElimination;

import RiotGamesDiscordBot.RiotGamesAPI.Containers.MatchResult.MatchResult;
import RiotGamesDiscordBot.Tournament.DiscordUser;
import RiotGamesDiscordBot.Tournament.Tournament;
import RiotGamesDiscordBot.Tournament.TournamentConfig;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;
import java.util.Map;

public class SingleEliminationTournament extends Tournament {

    public SingleEliminationTournament(TournamentConfig tournamentConfig, GuildMessageReceivedEvent event, Map<String, List<String>> teams, DiscordUser creator) {
        super(tournamentConfig, event,teams, creator);
    }

    @Override
    public void setup() {

    }

    @Override
    public void start() {

    }

    @Override
    public void advanceTournament(MatchResult matchResult) {

    }

    @Override
    public void endTournament() {

    }

    @Override
    public void idle() {

    }

    @Override
    public void resume() {

    }
}
