package RiotGamesDiscordBot.Commands.CommandHandlers;

import RiotGamesDiscordBot.RiotGamesAPI.Containers.*;
import RiotGamesDiscordBot.RiotGamesAPI.RiotGamesAPI;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.net.URL;
import java.util.*;

public class TournamentCommandHandler extends CommandHandler{
    private final Map<String, List<String>> teams;
    private final RiotGamesAPI riotAPI;

    public TournamentCommandHandler(GuildMessageReceivedEvent event, Iterator<String> messageIterator) {
        super(event, messageIterator);
        this.teams = new HashMap<>();
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
        this.getTeamsFromExcelFile(teamListFile);

        //Retrieve provider ID
        try {
            int providerID = riotAPI.getProviderID(new URL("https://callback.com"), Region.NA);
            int tournamentID = riotAPI.getTournamentID(providerID, "New Tournament");

            List<String> team1Members = this.teams.get("Team Fire");
            List<String> team2Members = this.teams.get("Team Rock");

            List<String> allowedSummonerIDs = new ArrayList<>();
            for (String teamMember : team1Members) {
                teamMember = teamMember.replace(" ", "%20");
                String summonerInfoJSON = riotAPI.getSummonerInfoByName(teamMember);
                allowedSummonerIDs.add(new Gson().fromJson(summonerInfoJSON, SummonerInfo.class).getEncryptedSummonerId());
            }

            for (String teamMember : team2Members) {
                teamMember = teamMember.replace(" ", "%20");
                String summonerInfoJSON = riotAPI.getSummonerInfoByName(teamMember);
                allowedSummonerIDs.add(new Gson().fromJson(summonerInfoJSON, SummonerInfo.class).getEncryptedSummonerId());
            }

            String tournamentCodes = riotAPI.getTournamentCodes(tournamentID, 5, allowedSummonerIDs, MapType.SUMMONERS_RIFT, PickType.TOURNAMENT_DRAFT, SpectatorType.ALL, 5);
            System.out.println("Provider ID: " + providerID);
            System.out.println("Tournament ID: " + tournamentID);
            System.out.println("Tournament Codes: " + tournamentCodes);


        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void getTeamsFromExcelFile(File teamListFile) {
        try {
            Workbook workbook = new XSSFWorkbook(new FileInputStream(teamListFile));
            Sheet teamListSheet = workbook.getSheetAt(0);

            //Get Team Names
            Row teamNamesRow = teamListSheet.getRow(0);
            for (int colIndex = 0; colIndex < teamNamesRow.getLastCellNum(); colIndex++) {
                teams.put(teamNamesRow.getCell(colIndex).getStringCellValue(), new ArrayList<>());
            }

            //Get Team Members
            for (int rowIndex = 1; rowIndex <= teamListSheet.getLastRowNum(); rowIndex++) {
                Row row = teamListSheet.getRow(rowIndex);
                Iterator<Cell> teamNameIter = teamNamesRow.cellIterator();
                for (int colIndex = 0; colIndex < row.getLastCellNum(); colIndex++) {
                    if (teamNameIter.hasNext()) {
                        String teamMember = row.getCell(colIndex).getStringCellValue();
                        this.teams.get(teamNameIter.next().getStringCellValue()).add(teamMember);
                    }
                    else {
                        break;
                    }

                }
            }
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
