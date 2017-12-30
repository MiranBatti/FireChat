package com.example.projekt.klientutveckling.firechat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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

    private addRoomToUser addRoomToUser = new addRoomToUser();
    private int roomNumber;
    private ArrayList<String> roomArray = new ArrayList<>();

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        mListView = (ListView) findViewById(R.id.lobby_list);
        mTextView = (TextView) findViewById(R.id.lobby_text);
        mImageButton = (ImageButton) findViewById(R.id.lobby_imageButton);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

        mAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    Log.d(TAG,"onAuthStateChanged:signed_in: " + user.getUid());
                    toastMessage("Successfully signed in eith: "+user.getEmail());
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
                roomNumber = 1;
                roomArray.clear();
                String username = dataSnapshot.child("username").getValue(String.class);
                mTextView.setText(username);
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toastMessage("IT WORKT");
                addRoomToUser.addRoom(userID);
            }
        });

    }

    private void showData(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds: dataSnapshot.getChildren()){

            if(dataSnapshot.child("rooms").child("room"+roomNumber).getValue()!=null) {
                String room = dataSnapshot.child("rooms").child("room" + roomNumber).getValue(String.class);
                roomNumber++;


                roomArray.add(room);

                ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, roomArray);
                mListView.setAdapter(adapter);

            }
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
