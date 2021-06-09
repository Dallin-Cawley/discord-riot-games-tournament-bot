package RiotGamesDiscordBot.Tournament;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public abstract class Tournament {
    private final long tournamentId;
    private final int  providerId;
    protected final List<Team> teams;
    protected final List<Match> activeMatches;
    protected final TextChannel messageChannel;

    public Tournament(long tournamentId, int providerId, GuildMessageReceivedEvent event, List<Team> teams) {
        this.tournamentId = tournamentId;
        this.providerId = providerId;
        this.teams = teams;
        this.activeMatches = new ArrayList<>();
        this.messageChannel = event.getChannel();
    }

    public Tournament(long tournamentId, int providerId, TextChannel channel, List<Team> teams) {
        this.tournamentId = tournamentId;
        this.providerId = providerId;
        this.messageChannel = channel;
        this.teams = teams;
        this.activeMatches = new ArrayList<>();
    }

    public abstract void setup();

    public abstract void start();

    public abstract void updateStandings(Match match);

    public abstract void endTournament();

    public int getProviderId() {
        return providerId;
    }

    public long getTournamentId() {
        return tournamentId;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Tournament) {
            return ((Tournament) object).tournamentId == this.tournamentId;
        }

        return false;
    }
}
