package RiotGamesDiscordBot.Commands.CommandHandlers;

import RiotGamesDiscordBot.RiotGamesAPI.Containers.Parameters.TournamentCodeParameters;
import RiotGamesDiscordBot.RiotGamesAPI.Containers.Region;
import RiotGamesDiscordBot.RiotGamesAPI.Containers.SummonerInfo;
import RiotGamesDiscordBot.RiotGamesAPI.RiotGamesAPI;
import RiotGamesDiscordBot.Tournament.RoundRobin.RoundRobinTournament;
import RiotGamesDiscordBot.Tournament.Team;
import RiotGamesDiscordBot.Tournament.Tournament;
import RiotGamesDiscordBot.Tournament.TournamentConfig;
import RiotGamesDiscordBot.Tournament.TournamentType;
import com.google.gson.Gson;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class TournamentCommandHandler extends CommandHandler{
    private final RiotGamesAPI riotAPI;

    public TournamentCommandHandler(GuildMessageReceivedEvent event, Iterator<String> messageIterator) {
        super(event, messageIterator);
        this.riotAPI = new RiotGamesAPI();
    }

    @Override
    public void handle() {
        List<Message.Attachment> attachments = this.event.getMessage().getAttachments();

        //Ensure an attachment has been attached
        if (attachments.isEmpty()) {
            event.getChannel().sendMessage("Please attach an Excel Document containing the teams participating").queue();
            return;
        }

        Message.Attachment teamListAttachment = attachments.get(0);

        File teamListFile = teamListAttachment.downloadToFile().join();
        String extension = FilenameUtils.getExtension(teamListFile.toString());

        //Ensure the attachment is the proper extension
        if (!extension.equals("xls") && !extension.equals("xlsx")) {
            event.getChannel().sendMessage("Please resend command with an attached .xls or .xlsx file").queue();
            return;
        }

        //Get the Team Names and Members
        List<Team> teams = this.getTeamsFromExcelFile(teamListFile);

        //Get provider and tournament ID
        try {
            int providerId = riotAPI.getProviderID(new URL("https://www.google.com"), Region.NA);
            long tournamentId = riotAPI.getTournamentID(providerId, "New Tournament");

            //Get Tournament Type
            TournamentConfig tournamentConfig = new TournamentConfig(teamListFile);
            Tournament tournament;
            switch (tournamentConfig.getTournamentType()) {
                case ROUND_ROBIN:
                    tournament = new RoundRobinTournament(providerId, tournamentId, tournamentConfig);
                    break;
                case SINGLE_ELIMINATION:
                default:
                    //TODO: Single Elimination Tournament
                    tournament = null;
            }

            tournament.setup(teams);
        }
        catch (MalformedURLException exception) {
            exception.printStackTrace();
        }

    }

    public List<Team> getTeamsFromExcelFile(File teamListFile) {
        try {
            Workbook workbook = new XSSFWorkbook(new FileInputStream(teamListFile));
            Sheet teamListSheet = workbook.getSheetAt(0);
            List<Team> teams = new ArrayList<>();

            //Get Team Names
            Row teamNamesRow = teamListSheet.getRow(0);
            for (int colIndex = 0; colIndex < teamNamesRow.getLastCellNum(); colIndex++) {
                teams.add(new Team(teamNamesRow.getCell(colIndex).getStringCellValue()));
            }

            //Get Team Members
            Gson gson = new Gson();
            for (int rowIndex = 1; rowIndex < 6; rowIndex++) {
                Row row = teamListSheet.getRow(rowIndex);
                for (int colIndex = 0; colIndex < row.getLastCellNum(); colIndex++) {
                    String teamMember = row.getCell(colIndex).getStringCellValue();

                    if (teamMember.isEmpty()) {
                        break;
                    }
                    SummonerInfo summonerInfo = gson.fromJson(riotAPI.getSummonerInfoByName(teamMember), SummonerInfo.class);
                    teams.get(colIndex).addMember(summonerInfo);
                }
            }

            return teams;
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }

        return null;
    }
}
