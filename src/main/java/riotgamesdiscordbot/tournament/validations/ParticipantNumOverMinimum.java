package riotgamesdiscordbot.tournament.validations;

import riotgamesdiscordbot.tournament.Team;
import riotgamesdiscordbot.tournament.Tournament;

/**
 * Validates that the total number of participants in the {@link Tournament} is over a minimum size
 */
public class ParticipantNumOverMinimum extends TournamentValidation {

    private final int minSize;

    public ParticipantNumOverMinimum(Tournament tournament, int minSize) {
        super(tournament);
        this.minSize = minSize;
    }

    /**
     * Validates that the number of participants in the {@link Tournament} is at least
     * {@link ParticipantNumOverMinimum#minSize}.
     *
     * @return A {@link ValidationStatus} containing {@link ValidationStatusCode#NOT_ENOUGH_PARTICIPANTS} if
     * participant size is below {@link ParticipantNumOverMinimum#minSize}, {@link ValidationStatusCode#OK} otherwise.
     */
    @Override
    public ValidationStatus validate() {

        int tournamentSize = 0;
        for (Team team : this.tournament) {
            tournamentSize += team.size();
        }

        if (tournamentSize < this.minSize) {
            ValidationStatus status = new ValidationStatus(ValidationStatusCode.NOT_ENOUGH_PARTICIPANTS);
            status.setRemoveTournament(true);

            return status;
        }

        return new ValidationStatus(ValidationStatusCode.OK);
    }
}
