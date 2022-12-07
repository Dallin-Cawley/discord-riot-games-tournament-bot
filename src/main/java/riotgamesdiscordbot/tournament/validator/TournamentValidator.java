package riotgamesdiscordbot.tournament.validator;

import riotgamesdiscordbot.tournament.Tournament;
import riotgamesdiscordbot.tournament.validations.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Validates a given tournament
 */
public abstract class TournamentValidator implements Validator {
    /** The {@link Tournament} being validated */
    protected final Tournament tournament;
    /** The {@link TournamentValidation}s that must pass */
    private final List<TournamentValidation> validations;

    /**
     * Sets up the {@link TournamentValidator}. Does add validations that all {@link Tournament}s must pass
     * regardless of type.
     *
     * @param tournament The {@link Tournament} being validated.
     */
    TournamentValidator(Tournament tournament) {
        this.tournament = tournament;
        this.validations = new ArrayList<>();

        this.addValidation(new UniqueTeamMembersValidation(tournament));
        this.addValidation(new TournamentSetUpValidation(tournament));
        this.addValidation(new ParticipantNumOverMinimum(tournament, 20));
        this.addValidation(new UniqueParticipants(tournament));
    }

    /**
     * Allows child classes to add validations unique to their tournament type.
     * @param validation The {@link Validation} to be added.
     */
    protected void addValidation(TournamentValidation validation) {
        this.validations.add(validation);
    }

    /**
     * Validates that a {@link Tournament} is aligns with all {@link TournamentValidation}s
     * @return A {@link ValidationStatus} containing offending invalid aspects of the {@link Tournament}, A
     * {@link ValidationStatus} with a {@link ValidationStatusCode#OK} otherwise.
     */
    @Override
    public ValidationStatus validate() {
        for (TournamentValidation validation : this.validations) {
            ValidationStatus status = validation.validate();

            if (status.getStatusCode() != ValidationStatusCode.OK) {
                return status;
            }
        }

        return new ValidationStatus(ValidationStatusCode.OK);
    }
}
