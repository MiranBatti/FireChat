package com.example.projekt.klientutveckling.firechat;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by ntn13dcm on 2017-12-15.
 */

public class CreateAccount extends ProgressActivity implements View.OnClickListener {

    private UserData userData = UserData.getUserData();
    private TextView userNameTextView;
    private EditText userNameTextField;
    private DatabaseReference mDatabase;
    private ArrayList<String> userList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        findViewById(R.id.username_button).setOnClickListener(this);

        userNameTextView = (TextView) findViewById(R.id.detail);
        userNameTextField = (EditText) findViewById(R.id.username);

        mDatabase = FirebaseDatabase.getInstance().getReference();



        //findViewById(R.id.username_button).setOnClickListener(this);

    }

    public void onClick(View view)
    {
        int i = view.getId();
        if (i == R.id.username_button) {
            userData.setRooms("katt");
            userData.setUsername(userNameTextField.getText().toString());
            writeNewUser(userData.getUsername(),userData.getEmail(),userData.getRooms());

        }
    }

    private void writeNewUser(String name, String email,String rooms){
        User user = new User(name,email,rooms);
        mDatabase.child("Users").child(name).setValue(user);
        mDatabase.child("Users").child(name).child("rooms").push().setValue(rooms);
        mDatabase.child("Users").child(name).child("rooms").push().setValue("mamma");
        mDatabase.child("Rooms").push().setValue(rooms);
        retrieve();
    }

    public ArrayList<String> retrieve()
    {
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                fetchData(dataSnapshot);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                fetchData(dataSnapshot);
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return userList;
    }

    private void fetchData(DataSnapshot dataSnapshot)
    {
        userList.clear();
        for (DataSnapshot ds : dataSnapshot.getChildren())
        {
            String name=ds.getValue(UserData.class).getUsername();
            userList.add(name);
        }
        mDatabase.child("Rooms").push().setValue(userList.get(0));
    }





}
