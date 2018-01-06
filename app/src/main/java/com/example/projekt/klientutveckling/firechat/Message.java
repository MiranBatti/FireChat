package com.example.projekt.klientutveckling.firechat;

/**
 * Created by ofk14mbi on 2017-12-21.
 */

public class Message
{
    private String message;
    private long time;
    private String from;

    /**
     * Model for messages. "Seen" feature not implemented.
     *
     * @param message
     * @param time
     * @param from
     */
    public Message(String message, long time, String from)
    {
        this.message = message;
        this.time = time;
        this.from = from;
    }

    /**
     * empty constructor required by firebase
     */
    public Message(){}

    public String getMessage() {return message;}
    public void setMessage(String message) {
        this.message = message;
    }

    public long getTime() {
        return time;
    }
    public void setTime(long time) {
        this.time = time;
    }

    public String getFrom() {return from;}
    public void setFrom(String from) {this.from = from;}
}
