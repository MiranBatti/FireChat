package com.example.projekt.klientutveckling.firechat;

/**
 * Created by Daniel on 2017-12-19.
 */

public class User {

    public String username;
    public String email;
    public String rooms;

    public User(){

    }

    public User(String username,String email,String rooms){
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
    public String getRooms(){
        return rooms;
    }
    public void addRoom(String room){

        rooms = rooms+","+room;
    }
}
