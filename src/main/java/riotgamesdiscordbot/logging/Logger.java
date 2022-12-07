package riotgamesdiscordbot.logging;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    public static void log(String message, Level level) {
        //Current Thread
        String threadName = Thread.currentThread().getName();

        //Current Time
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("America/Denver"));
        DateTimeFormatter inFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a");
        String formattedDate = zonedDateTime.format(inFormat);

        String logInfo = ConsoleColors.CYAN + "[ " + threadName + " : " + formattedDate + " ] " + ConsoleColors.RESET;

        String levelInfo = switch (level) {
            case WARNING -> ConsoleColors.YELLOW_BOLD + "WARNING" + ConsoleColors.RESET;
            case ERROR -> ConsoleColors.RED_BOLD + "ERROR" + ConsoleColors.RESET;
            case INFO -> ConsoleColors.GREEN_BOLD + "INFO" + ConsoleColors.RESET;
        };

        System.out.println(logInfo + levelInfo + " " + message);
    }
}
