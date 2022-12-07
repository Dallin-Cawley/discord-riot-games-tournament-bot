package riotgamesdiscordbot.eventhandling;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class RemoveExpiredEventsTest {

    private RemoveExpiredEvents removeExpiredEvents;

    @Test
    void shouldRemoveExpiredEvents() {
        Map<String, Event> events = new HashMap<>();
        EventMock event = new EventMock("mock event");
        event.setResolved(true);
        events.put("id", new EventMock("mock event"));

        this.removeExpiredEvents = new RemoveExpiredEvents(events);
        Assertions.assertEquals(events.size(), 1);

        this.removeExpiredEvents.performStep();
        Assertions.assertEquals(events.size(), 0);
    }

}