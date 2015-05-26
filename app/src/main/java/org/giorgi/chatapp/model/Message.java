package org.giorgi.chatapp.model;

public class Message {

    public static final long MY_ID = -1;

    private final long destinationId;
    private final long sourceId;
    private final String message;
    private final String time;

    public Message(long source, long destination, String text, String time) {
        this.sourceId = source;
        this.destinationId = destination;
        this.message = text;
        this.time = time;
    }

    public long getSourceId() {
        return sourceId;
    }

    public long getDestinationId() {
        return destinationId;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }

    public boolean isIncoming() {
        return (destinationId == MY_ID);
    }
}
