package riotgamesdiscordbot.tournament.validations;

import riotgamesdiscordbot.tournament.Tournament;

public abstract class TournamentValidation implements Validation {

    protected final Tournament tournament;

    TournamentValidation(Tournament tournament) {
        this.tournament = tournament;
    }
}
