package riotgamesdiscordbot.eventhandling;

public abstract class ErrorEvent extends MessageEvent {

    protected String message;

    public ErrorEvent(String messageTitle) {
        super(messageTitle);
    }

    public String getMessage() {
        return message;
    }
}
