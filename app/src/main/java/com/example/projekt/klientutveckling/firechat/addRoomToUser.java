package com.example.projekt.klientutveckling.firechat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by denna on 2017-12-29.
 */

public class addRoomToUser {

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private int roomNumber =1;


    public void addRoom(String userId){

        Map roomMap =  new HashMap();
        roomMap.put("room"+roomNumber,"Room1");
        roomNumber++;
        mDatabase.child("users").child(userId).child("rooms").updateChildren(roomMap);

    }
}
