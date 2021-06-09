package RiotGamesDiscordBot.Tournament.RoundRobin.Events;

import RiotGamesDiscordBot.EventHandling.UserHandleableEvent;
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

public class MemberOnBothTeamsEvent implements UserHandleableEvent {
    private final ZonedDateTime expires;
    private final TextChannel textChannel;
    private final String eventID;
    private final SummonerInfo duplicateMember;
    private final Team teamOne;
    private final Team teamTwo;
    private final Suspendable suspendable;

    public MemberOnBothTeamsEvent(TextChannel textChannel, String eventID, SummonerInfo duplicateMember, Team teamOne, Team teamTwo, Suspendable suspendable) {
        this.expires = ZonedDateTime.now().plus(2, ChronoUnit.HOURS);
        this.textChannel = textChannel;
        this.eventID = eventID;
        this.duplicateMember = duplicateMember;
        this.teamOne = teamOne;
        this.teamTwo = teamTwo;
        this.suspendable = suspendable;
    }



    @Override
    public void handle(Iterator<String> args) {
        RiotGamesAPI riotGamesAPI = new RiotGamesAPI();

        boolean getSummonerName = false;
        boolean getTeamName = false;
        StringBuilder summonerName = new StringBuilder();
        StringBuilder teamName = new StringBuilder();
        while (args.hasNext()) {
            String arg = args.next();
            if (arg.equals("-s")) {
                getTeamName = false;
                getSummonerName = true;
                continue;
            }
            else if(arg.equals("-tn")) {
                getSummonerName = false;
                getTeamName = true;
                continue;
            }

            if (getSummonerName) {
                summonerName.append(arg).append(" ");
            }
            else if (getTeamName) {
                teamName.append(arg).append(" ");
            }
        }

        String trimmedSummonerName = summonerName.toString().trim();
        String trimmedTeamName = teamName.toString().trim();



        if (this.teamOne.getTeamName().equals(trimmedTeamName)) {
            this.teamOne.getMembers().remove(this.duplicateMember);
            try {
                String urlReadySummonerName = trimmedSummonerName.replace(" ", "%20");
                SummonerInfo summonerInfo = new Gson().fromJson(riotGamesAPI.getSummonerInfoByName(urlReadySummonerName), SummonerInfo.class);
                this.teamOne.getMembers().add(summonerInfo);
            }
            catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        else if (this.teamTwo.getTeamName().equals(trimmedTeamName)) {
            this.teamTwo.getMembers().remove(this.duplicateMember);
            try {
                String urlReadySummonerName = trimmedSummonerName.replace(" ", "%20");
                SummonerInfo summonerInfo = new Gson().fromJson(riotGamesAPI.getSummonerInfoByName(urlReadySummonerName), SummonerInfo.class);
                this.teamTwo.getMembers().add(summonerInfo);
            }
            catch (IOException exception) {
                exception.printStackTrace();
            }
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

        String stringBuilder = this.duplicateMember.getSummonerName() + " seems to be on two teams." + "\n\n\t" +

                teamOne.getTeamName() + "\t" + teamTwo.getTeamName() + "\n\n" +

                "Please replace the Summoner with another within 2 hours so that the tournament may continue" +
                " using the following command...\n" +

                "\n\t~lol -t --rectify [ Event ID ] -s [ New Summoner Name ] -tn [ Team Name ]\n\n" +

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
