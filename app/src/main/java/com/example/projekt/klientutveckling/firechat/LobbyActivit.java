package com.example.projekt.klientutveckling.firechat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by ofk14den on 2017-12-18.
 */

public class LobbyActivit extends AppCompatActivity{
    private static final String TAG = "UserInformation";

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListner;
    private DatabaseReference mRef;

    private String userID;
    private  ListView mListView;
    private TextView mTextView;
    private ImageButton mImageButton;
    private TextView mOldchatlobbyTextView;

    private String oldChatRoomName;
    private ArrayList<String> oldChatRoomNameArray = new ArrayList<>();

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        mListView = (ListView) findViewById(R.id.lobby_list);
        mTextView = (TextView) findViewById(R.id.lobby_text);
        mOldchatlobbyTextView = (TextView) findViewById(R.id.oldchatlobby_text);
        mImageButton = (ImageButton) findViewById(R.id.lobby_imageButton);
        mListView.setScrollContainer(true);


        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();
        mOldchatlobbyTextView.setText("Old Chats");

        mAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    Log.d(TAG,"onAuthStateChanged:signed_in: " + user.getUid());
                    toastMessage("Successfully signed in with: "+user.getEmail());
                }
                else {
                    Log.d(TAG,"onAuthStateChanged:signed_out");
                    toastMessage("Successfully signed out");
                }
            }
        };


        mRef.child("users").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                oldChatRoomNameArray.clear();
               String username = dataSnapshot.child("username").getValue(String.class);
                mTextView.setText(username);
                dataSnapshot = dataSnapshot.child("rooms");
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LobbyActivit.this, CreatChatActivity.class);
                startActivity(intent);
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openOldChat(oldChatRoomNameArray.get(position));
            }
        });

    }

    private void openOldChat(String roomName){
      this.oldChatRoomName = roomName;
        mRef.child("users").child(userID).child("rooms").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Intent intent = new Intent(LobbyActivit.this, ChatActivity.class);
                intent.putExtra("messagesRoomName", dataSnapshot.child(oldChatRoomName).getValue().toString());
                intent.putExtra("roomName", dataSnapshot.child(oldChatRoomName).getKey().toString());
                startActivity(intent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }




    private void showData(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds: dataSnapshot.getChildren()){


                String room = ds.getKey().toString();



                oldChatRoomNameArray.add(room);

                ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.lobby_list_items, oldChatRoomNameArray);
                mListView.setAdapter(adapter);



        }


    }

    @Override
    public void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListner);
    }
    @Override
    public void onStop(){
        super.onStop();
        if(mAuthListner != null){
            mAuth.removeAuthStateListener(mAuthListner);
        }
    }

    private void toastMessage(String message){

        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }



}
