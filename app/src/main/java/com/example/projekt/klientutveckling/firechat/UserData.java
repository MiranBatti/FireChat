package com.example.projekt.klientutveckling.firechat;

/**
 * Created by Daniel on 2017-12-18.
 */

public class UserData {
    private String email;
    private String username;
    private static UserData userData = new UserData();
    private String rooms;
    private UserData(){

    }

    public static UserData getUserData(){
        return userData;
    }

    public void setEmail(String email){
        this.email = email;

    }

    public String getEmail(){
        return email;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public String getRooms(){
        return rooms;
    }


    public void setRooms(String room){

        rooms = rooms+","+room;
    }
}
