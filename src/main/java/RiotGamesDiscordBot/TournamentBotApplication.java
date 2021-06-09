package RiotGamesDiscordBot;

import RiotGamesDiscordBot.Commands.DiscordBotCommands;
import RiotGamesDiscordBot.Tournament.TournamentManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.TextChannel;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import javax.security.auth.login.LoginException;
import java.util.List;

@SpringBootApplication
public class TournamentBotApplication {
    private JDA discordAPI;
    private final TournamentManager tournamentManager = new TournamentManager();

    public static void main(String[] args) {
        SpringApplication.run(TournamentBotApplication.class, args);
    }


    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
        };
    }

    @Bean(name = "tournamentBot")
    public JDA getDiscordAPI() throws LoginException, InterruptedException {
        JDABuilder builder = JDABuilder.createDefault(System.getenv("DISCORD_BOT_TOKEN"));
        builder.setActivity(Activity.of(Activity.ActivityType.LISTENING, "You Bozos"));
        this.discordAPI = builder.build();
        this.discordAPI.awaitReady();

        this.discordAPI.addEventListener(new DiscordBotCommands(this.discordAPI, this.tournamentManager));

        return this.discordAPI;
    }

    @Bean(name = "textChannels")
    public List<TextChannel> getTextChannels() {
        return this.discordAPI.getTextChannels();
    }
}

