package RiotGamesDiscordBot.Commands.CommandHandlers;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;

public class TournamentCommandHandler extends CommandHandler{
    private final Map<String, List<String>> teams;

    public TournamentCommandHandler(GuildMessageReceivedEvent event, Iterator<String> messageIterator) {
        super(event, messageIterator);
        this.teams = new HashMap<>();
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

        //Ensure the attachment is the proper format
        if (!extension.equals(".xls") && !extension.equals(".xlsx")) {
            event.getChannel().sendMessage("Please resend command with an attached .xls or .xlsx file").queue();
            return;
        }

        try {
            //TODO: Parse over Excel document and populate teams
            Workbook workbook = new XSSFWorkbook(new FileInputStream(teamListFile));
            Sheet teamListSheet = workbook.getSheetAt(0);
            Row teamNames = teamListSheet.getRow(0);
            for (int rowIndex = 1; rowIndex < teamListSheet.getLastRowNum(); rowIndex++) {
                Row row = teamListSheet.getRow(rowIndex);
                List<String> teamMembersList = new ArrayList<>();
                for (int colIndex = 0; colIndex < row.getLastCellNum(); colIndex++) {
                    String teamName = teamNames.getCell(colIndex).getStringCellValue();

                }
            }
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
