package RiotGamesDiscordBot.Tournament.RoundRobin.Events;

import RiotGamesDiscordBot.EventHandling.UserHandleableEvent;
import RiotGamesDiscordBot.Logging.Level;
import RiotGamesDiscordBot.Logging.Logger;
import RiotGamesDiscordBot.RiotGamesAPI.Containers.SummonerInfo;
import RiotGamesDiscordBot.RiotGamesAPI.RiotGamesAPI;
import RiotGamesDiscordBot.Tournament.Suspendable;
import RiotGamesDiscordBot.Tournament.Team;
import com.google.gson.Gson;
import net.dv8tion.jda.api.entities.TextChannel;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;

public class TeamMemberDuplicateEvent implements UserHandleableEvent {
    private final TextChannel textChannel;
    private final SummonerInfo duplicateMember;
    private final Team team;
    private final String eventID;
    private final ZonedDateTime expires;
    private final Suspendable suspendable;


    public TeamMemberDuplicateEvent(TextChannel textChannel, SummonerInfo duplicateMember, Team team, String eventID, Suspendable suspendable) {
        this.textChannel = textChannel;
        this.duplicateMember = duplicateMember;
        this.team = team;
        this.eventID = eventID;
        this.expires = ZonedDateTime.now().plus(2, ChronoUnit.HOURS);
        this.suspendable = suspendable;
    }


    @Override
    public void handle(Iterator<String> args) {
        Logger.log("Handling TeamMemberDuplicateEvent", Level.INFO);
        args.next();
        StringBuilder summonerName = new StringBuilder();
        while (args.hasNext()) {
            summonerName.append(args.next());
        }

        String trimmedSummonerName = summonerName.toString().trim();
        RiotGamesAPI riotGamesAPI = new RiotGamesAPI();

        this.team.getMembers().remove(this.duplicateMember);
        try {
            Logger.log("Attempting to retrieve SummonInfo for " + trimmedSummonerName, Level.INFO);
            String urlReadySummonerName = trimmedSummonerName.replace(" ", "%20");
            SummonerInfo summonerInfo = new Gson().fromJson(riotGamesAPI.getSummonerInfoByName(urlReadySummonerName), SummonerInfo.class);
            Logger.log("Successfully retrieved SummonerInfo for " + trimmedSummonerName,Level.INFO);
            Logger.log("Adding " + trimmedSummonerName + " to " + team.getTeamName() + "'s member list", Level.INFO);
            this.team.getMembers().add(summonerInfo);
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public boolean isExpired() {
        return ZonedDateTime.now().getNano() > this.expires.getNano();
    }

    @Override
    public void expire() {

    }

    @Override
    public String getWarning() {
        return null;
    }

    @Override
    public void sendMessage() {

        String stringBuilder = this.duplicateMember.getSummonerName() + " seems to be on " + team.getTeamName() +
                " more than once.\n\n" +

                "Please replace the Summoner with another within 2 hours so that the tournament may continue" +
                " using the following command...\n" +

                "\n\t~lol -t --rectify [ Event ID ] -s [ New Summoner Name ]\n\n" +

                "If there is no replacement within the time given the tournament will not start and be removed. " +
                "You may reattempt tournament creation after this removal with an updated tournament sheet." +
                "\n\n\tEvent ID is : " + this.eventID;

        this.textChannel.sendMessage(stringBuilder).queue();
    }

    @Override
    public void resume() {
        this.suspendable.resume();
    }
}
