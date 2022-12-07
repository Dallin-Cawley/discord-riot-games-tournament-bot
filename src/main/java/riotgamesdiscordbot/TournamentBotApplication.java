package riotgamesdiscordbot;

import riotgamesdiscordbot.commands.DiscordBotCommands;
import riotgamesdiscordbot.tournament.TournamentManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import javax.security.auth.login.LoginException;

@SpringBootApplication
public class TournamentBotApplication {
    private static JDA discordAPI;

    public static void main(String[] args) throws InterruptedException {
        startDiscordAPI();
        TournamentManager.getInstance();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> TournamentManager.getInstance().shutdown()));
        SpringApplication.run(TournamentBotApplication.class, args);
    }


    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
        };
    }

    public static void startDiscordAPI() throws InterruptedException {
        if (discordAPI == null) {
            JDABuilder builder = JDABuilder.createDefault(System.getenv("DISCORD_BOT_TOKEN"));
            builder.setActivity(Activity.of(Activity.ActivityType.LISTENING, "You Bozos"));
            builder.setEnabledIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.DIRECT_MESSAGES,
                    GatewayIntent.GUILD_MESSAGES,
                    GatewayIntent.GUILD_VOICE_STATES,
                    GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
                    GatewayIntent.SCHEDULED_EVENTS);
            discordAPI = builder.build();
            discordAPI.awaitReady();

            discordAPI.addEventListener(new DiscordBotCommands(discordAPI));
        }

    }

}

