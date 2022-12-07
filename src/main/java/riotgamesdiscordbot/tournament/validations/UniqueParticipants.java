package riotgamesdiscordbot.tournament.validations;

import riotgamesdiscordbot.riotgamesapi.containers.SummonerInfo;
import riotgamesdiscordbot.tournament.roundrobin.events.containers.MemberOnMultipleTeamsContainer;
import riotgamesdiscordbot.tournament.Team;
import riotgamesdiscordbot.tournament.Tournament;

import java.util.ArrayList;
import java.util.List;

/**
 * Validates that each participant is unique in a given {@link riotgamesdiscordbot.tournament.Tournament}
 */
public class UniqueParticipants extends TournamentValidation {
    public UniqueParticipants(Tournament tournament) {
        super(tournament);
    }

    /**
     * Searches each participant of the {@link UniqueParticipants#tournament} for participants who are listed
     * on multiple teams.
     *
     * @return A {@link ValidationStatus} containing the offending participant if they are listed on multiple teams,
     * a {@link ValidationStatus} containing {@link ValidationStatusCode#OK} otherwise.
     */
    @Override
    public ValidationStatus validate() {
        List<MemberOnMultipleTeamsContainer> members = new ArrayList<>();
        for (Team team: this.tournament) {

            for (SummonerInfo summonerInfo : team.getMembers()) {
                if (members.contains(summonerInfo)) {
                    return this.getFailingValidationStatus(members.get(members.indexOf(summonerInfo)));
                }
                else {
                    members.add(new MemberOnMultipleTeamsContainer(summonerInfo));
                }
            }
        }

        return new ValidationStatus(ValidationStatusCode.OK);
    }

    /**
     * Creates and returns the failing {@link ValidationStatus}
     *
     * @param member The participant that is on multiple teams.
     * @return The failing {@link ValidationStatus}
     */
    ValidationStatus getFailingValidationStatus(MemberOnMultipleTeamsContainer member) {
        ValidationStatus status = new ValidationStatus(ValidationStatusCode.DUPLICATE_PARTICIPANTS_IN_TOURNAMENT);
        status.addResource("MemberOnMultipleTeamsContainer", member);
        status.setRemoveTournament(true);

        return status;
    }
}
