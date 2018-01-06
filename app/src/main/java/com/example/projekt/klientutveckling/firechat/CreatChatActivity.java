package com.example.projekt.klientutveckling.firechat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by denna on 2017-12-30.
 */

public class CreatChatActivity extends AppCompatActivity {
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;

    private String usernameId;
    private ArrayList<String> firendsUsernameId = new ArrayList();
    private long roomNumber;
    private String bigRoomName;

    private EditText roomname;
    private EditText firendsUsername;
    private Button creatChatButton;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creatchat);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        usernameId = mAuth.getCurrentUser().getUid();

        roomname = (EditText) findViewById(R.id.createchat_roomname);
        firendsUsername = (EditText) findViewById(R.id.createchat_firendusername);
        creatChatButton = (Button) findViewById(R.id.createchat_creatchatbutton);

        getRoomName();


        creatChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(firendsUsernameId.isEmpty()) {
                    firendsUsernameId.clear();
                }
                controlIfFriendExist();
            }
        });

    }

    private void creatRoom(){
        {
            bigRoomName= usernameId+(int)roomNumber;

            addRoom(usernameId,roomname.getText().toString(),bigRoomName);
            for(String friendid : this.firendsUsernameId){
                addRoom(friendid,roomname.getText().toString(),bigRoomName);
            }
            creatRoom(bigRoomName);

        }

    }

// shold do so you can add more then one friend at the time
    private void controlIfFriendExist(){

        mRef.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean firendExist= false;
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    if(ds.child("username").getValue(String.class).equals(firendsUsername.getText().toString())){
                        firendsUsernameId.add(ds.getKey().toString());
                        firendExist = true;
                    }
                }
                if(firendExist) {
                    creatRoom();
                }
                else {
                    toastMessage("User do not exist");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void addRoom(String userId,String roomName,String bigRoomName){

        Map roomMap =  new HashMap();
        roomMap.put(roomName,bigRoomName);
        mRef.child("users").child(userId).child("rooms").updateChildren(roomMap);

    }
    private void creatRoom(String bigRoomName){

        Intent intent = new Intent(CreatChatActivity.this, ChatActivity.class);
        intent.putExtra("messagesRoomName",bigRoomName);
        intent.putExtra("roomName",roomname.getText().toString());
        startActivity(intent);

    }


    private void getRoomName(){

        mRef.child("users").child(usernameId).child("rooms").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                roomNumber=dataSnapshot.getChildrenCount();



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    private void toastMessage(String message){

        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }



}
