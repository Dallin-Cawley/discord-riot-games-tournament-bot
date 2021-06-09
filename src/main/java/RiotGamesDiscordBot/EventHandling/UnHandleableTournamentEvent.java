package RiotGamesDiscordBot.EventHandling;

import RiotGamesDiscordBot.Tournament.Tournament;

public interface UnHandleableTournamentEvent extends UnHandleableEvent {

    public Tournament getTournament();
}
