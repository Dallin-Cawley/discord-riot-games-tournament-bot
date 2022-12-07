package riotgamesdiscordbot.eventhandling;

public abstract class MessageEvent extends Event {

    protected String message;

    public MessageEvent(String messageTitle) {
        super(messageTitle);

        this.message = "";
    }
    public void messageSent() {
        this.resolved = true;
    }

    public String getMessage() {
        return this.message + "\n\nEvent ID: " + this.eventId;
    }

    public void updateMessage(String message) {
        this.resolved = false;
        this.message = message;
    }
}
