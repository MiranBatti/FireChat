package com.example.projekt.klientutveckling.firechat;

/**
 * Created by Daniel on 2017-12-18.
 */

public class UserData {
    private String email;
    private static UserData userData = new UserData();

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
}
