package riotgamesdiscordbot.eventhandling;

import riotgamesdiscordbot.logging.discordlog.DiscordLogger;
import riotgamesdiscordbot.workerStep.WorkerStep;

import java.util.Map;
import java.util.Set;

public class SendMessageEvents implements WorkerStep {

    private final DiscordLogger discordLogger;
    private final Map<String, Event> events;

    public SendMessageEvents(DiscordLogger discordLogger, Map<String, Event> events) {
        this.discordLogger = discordLogger;
        this.events = events;
    }
    @Override
    public void performStep() {
        Set<String> keys = this.events.keySet();

        for (String key : keys) {
            Event event = this.events.get(key);

            if (event instanceof MessageEvent) {
                if (!event.isResolved()) {
                    this.discordLogger.sendMessage(event.getEventTitle(), ((MessageEvent) event).getMessage());
                    ((MessageEvent) event).messageSent();
                }
            }
        }
    }
}
