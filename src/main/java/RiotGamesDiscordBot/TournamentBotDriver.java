package RiotGamesDiscordBot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class TournamentBotDriver {
    @Resource(name = "tournamentBot")
    private JDA discordAPI;

    @Resource(name = "textChannels")
    private List<TextChannel> textChannels;


    @PostMapping(value = "/sendMessage", params = {"message"})
    public @ResponseBody
    String sendMessage(@RequestParam(value = "message") String message) {
        this.textChannels.get(0).sendMessage(message).queue();

        return "Message sent";
    }
}
