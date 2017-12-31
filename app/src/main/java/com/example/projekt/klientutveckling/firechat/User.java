package com.example.projekt.klientutveckling.firechat;

import java.util.Map;

/**
 * Created by Daniel on 2017-12-19.
 */

public class User {

    public String username;
    public String email;
    public Map<String, String> rooms;

    public User(){

    }

    public User(String username,String email,Map<String, String> rooms){
        this.username = username;
        this.email = email;
        this.rooms = rooms;
    }

    public String getUsername(){
        return username;
    }
    public void setUsername(String username){
        this.username = username;
    }


    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public Map<String, String> getRooms(){return rooms;}
    public void setRooms(Map<String, String> rooms) {this.rooms = rooms;}

    public boolean roomExist()
    {
        if(rooms != null)
            return true;
        else
            return false;
    }
}
