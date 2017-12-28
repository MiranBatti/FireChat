package com.example.projekt.klientutveckling.firechat;

/**
 * Created by ofk14den on 2017-12-28.
 */

public class DbUserNameCreater {

    private String username;
    private String email;


    public String getDbUsername(String email){

        this.email = email;


        return getDbUsernamehelper();
    }


    private String getDbUsernamehelper(){

        username = email.replace("@","");

        username = username.replace(".","");

        return username;
    }


}
