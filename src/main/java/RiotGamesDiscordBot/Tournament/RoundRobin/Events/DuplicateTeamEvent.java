package RiotGamesDiscordBot.Tournament.RoundRobin.Events;

import RiotGamesDiscordBot.EventHandling.UnHandleableTournamentEvent;
import RiotGamesDiscordBot.Logging.DiscordLog.DiscordTextUtils;
import RiotGamesDiscordBot.Tournament.Team;
import RiotGamesDiscordBot.Tournament.Tournament;
import net.dv8tion.jda.api.entities.TextChannel;

public class DuplicateTeamEvent implements UnHandleableTournamentEvent {
    private final Tournament tournament;
    private final TextChannel channel;
    private final Team duplicateTeam;

    public DuplicateTeamEvent(Tournament tournament, TextChannel channel, Team duplicateTeam) {
        this.tournament = tournament;
        this.channel = channel;
        this.duplicateTeam = duplicateTeam;
    }

    @Override
    public void sendErrorMessage() {
        // Build Error Message

        String title = DiscordTextUtils.colorRed("DUPLICATE TEAM ERROR");
        String errorMessage = title + duplicateTeam.getTeamName() + " " +
                "is listed in the Tournament Configuration excel file multiple times.\n\n" +
                "There is no Bot Command that can rectify this event. Because of this, the tournament will be removed. " +
                "Please remove or replace the duplicate team and try again.";
        this.channel.sendMessage(errorMessage).queue();
    }

    @Override
    public Tournament getTournament() {
        return tournament;
    }
}
