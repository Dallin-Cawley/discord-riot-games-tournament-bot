package riotgamesdiscordbot.tournament.validations;

import riotgamesdiscordbot.tournament.Tournament;

public class TournamentSetUpValidation extends TournamentValidation {

    public TournamentSetUpValidation(Tournament tournament) {
        super(tournament);
    }

    /**
     * Determines if the {@link Tournament} has been set up.
     * @return A {@link ValidationStatus} containing {@link ValidationStatusCode#NOT_SETUP},
     * {@link ValidationStatusCode#OK} otherwise.
     */
    @Override
    public ValidationStatus validate() {
        if (!tournament.isSetup()) {
            return new ValidationStatus(ValidationStatusCode.NOT_SETUP);
        }

        return new ValidationStatus(ValidationStatusCode.OK);
    }
}
