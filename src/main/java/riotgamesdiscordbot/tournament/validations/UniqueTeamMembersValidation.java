package riotgamesdiscordbot.tournament.validations;

import riotgamesdiscordbot.riotgamesapi.containers.SummonerInfo;
import riotgamesdiscordbot.tournament.Team;
import riotgamesdiscordbot.tournament.Tournament;

import java.util.List;

/**
 * Validates that each member of each team is unique in a given {@link Tournament}
 */
public class UniqueTeamMembersValidation extends TournamentValidation {
    public UniqueTeamMembersValidation(Tournament tournament) {
        super(tournament);
    }

    /**
     * Iterates over each {@link Team} in the tournament and determines if any team member is on the same team more than
     * once.
     * @return A {@link ValidationStatus} containing the offending team and member, {@link ValidationStatusCode#OK}
     * otherwise
     */
    @Override
    public ValidationStatus validate() {

        for (Team team : this.tournament) {

            int duplicateMemberIndex = this.duplicateTeamMembers(team);

            if (duplicateMemberIndex >= 0) {
                ValidationStatus status = new ValidationStatus(ValidationStatusCode.DUPLICATE_MEMBERS_ON_TEAM);
                status.addResource("team", team);
                status.addResource("duplicateMember", team.getMembers().get(duplicateMemberIndex));
                status.setRemoveTournament(true);
                return status;
            }
        }

        return new ValidationStatus(ValidationStatusCode.OK);
    }

    /**
     * Make sure that each member of a {@link Team} is unique. No repeat members
     *
     * @param team Team - the team that is being validated
     *
     * @return Index of duplicate member, -1 otherwise.
     */
    private int duplicateTeamMembers(Team team) {
        List<SummonerInfo> members = team.getMembers();

        for (int i = 0; i < members.size(); i++) {
            SummonerInfo member = members.get(i);

            for (int j = 0; j < members.size(); j++) {
                // Don't let member be compared with itself
                if (j == i) {
                    continue;
                }
                SummonerInfo anotherMember = members.get(j);
                // If there is a repeat, the team is not valid
                if (member.equals(anotherMember)) {
                    return j;
                }
            }
        }

        return -1;
    }
}
