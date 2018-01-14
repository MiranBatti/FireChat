package com.example.projekt.klientutveckling.firechat;

/**
 * Created by ofk14mbi on 2017-12-21.
 */

public class Message
{
    private String message;
    private long time;
    private String from;
    private String type;

    /**
     * Model for messages. "Seen" feature not implemented.
     *
     * @param message
     * @param time
     * @param from
     */
    public Message(String message, long time, String from, String type)
    {
        this.message = message;
        this.time = time;
        this.from = from;
        this.type = type;
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

    public String getType() {return type;}
    public void setType(String type) {this.type = type;}
}
