package com.example.projekt.klientutveckling.firechat;

/**
 * Created by ofk14mbi on 2017-12-21.
 */

public class Message
{
    private String message;

    private long time;

    public Message(String message, long time)
    {
        this.message = message;
        this.time = time;
    }

    public Message(){}

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
