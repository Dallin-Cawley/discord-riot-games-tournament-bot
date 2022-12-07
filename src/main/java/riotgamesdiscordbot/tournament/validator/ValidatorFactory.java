package riotgamesdiscordbot.tournament.validator;

import riotgamesdiscordbot.tournament.roundrobin.RoundRobinTournament;
import riotgamesdiscordbot.tournament.Tournament;
import riotgamesdiscordbot.tournament.validations.ValidationStatus;
import riotgamesdiscordbot.tournament.validations.ValidationStatusCode;

public class ValidatorFactory {

    public static Validator getValidator(Tournament tournament) {
        if (tournament.getClass().equals(RoundRobinTournament.class)) {
            return new RoundRobinTournamentValidator(tournament);
        }

        return () -> new ValidationStatus(ValidationStatusCode.OK);
    }

}
