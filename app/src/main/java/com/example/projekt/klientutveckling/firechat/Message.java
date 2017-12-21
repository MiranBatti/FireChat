package com.example.projekt.klientutveckling.firechat;

/**
 * Created by ofk14mbi on 2017-12-21.
 */

public class Message
{
    private String message;

    private String time;

    public Message(String message, String time)
    {
        this.message = message;
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
