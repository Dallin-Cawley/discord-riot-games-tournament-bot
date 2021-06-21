package RiotGamesDiscordBot;

import RiotGamesDiscordBot.Logging.Level;
import RiotGamesDiscordBot.Logging.Logger;
import RiotGamesDiscordBot.RiotGamesAPI.Containers.MatchResult.MatchResult;
import RiotGamesDiscordBot.Tournament.TournamentManager;
import com.google.gson.Gson;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@RestController
public class TournamentBotDriver {
    @Resource(name = "tournamentBot")
    private JDA discordAPI;

    @Resource(name = "textChannels")
    private List<TextChannel> textChannels;

    @Resource(name = "tournamentManager")
    private TournamentManager tournamentManager;


    @PostMapping(value = "/sendMessage", params = {"message"})
    public @ResponseBody
    String sendMessage(@RequestParam(value = "message") String message) {
        this.textChannels.get(0).sendMessage(message).queue();

        return "Message sent";
    }

    @PostMapping(value = "/matchResult")
    public @ResponseBody
    String matchResult(@RequestBody String matchResultJson) {
        System.out.println("MatchResultJson: " + matchResultJson);
        MatchResult matchResult = new Gson().fromJson(matchResultJson, MatchResult.class);

        tournamentManager.advanceTournament(matchResult);
        return "Done";
    }

    @RequestMapping(value = "/riot.txt")
    public ResponseEntity<Object> downloadFile() throws IOException  {
        String filename = "./riot.txt";
        File file = new File(filename);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        HttpHeaders headers = new HttpHeaders();
        Logger.log("Retrieved file", Level.INFO);

        headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getName()));
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        Logger.log("Sending response", Level.INFO);
        return ResponseEntity.ok().headers(headers).contentLength(file.length()).contentType(
        MediaType.parseMediaType("application/txt")).body(resource);
    }
}
