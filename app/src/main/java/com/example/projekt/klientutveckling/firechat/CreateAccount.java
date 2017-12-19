package com.example.projekt.klientutveckling.firechat;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by ntn13dcm on 2017-12-15.
 */

public class CreateAccount extends ProgressActivity implements View.OnClickListener {

    private UserData userData = UserData.getUserData();
    private TextView userNameTextView;
    private EditText userNameTextField;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        findViewById(R.id.username_button).setOnClickListener(this);
        userNameTextView = (TextView) findViewById(R.id.detail);
        userNameTextField = (EditText) findViewById(R.id.field_email);
        mDatabase = FirebaseDatabase.getInstance().getReference();



        //findViewById(R.id.username_button).setOnClickListener(this);

    }

    public void onClick(View view)
    {
        int i = view.getId();
        if (i == R.id.username_button) {

            writeNewUser("Daniel","ntn13dcm@student.hig.se");

        }
    }

    private void writeNewUser(String name,String email){
        User user = new User(name,email);

        mDatabase.child("Users").setValue(user);
    }



}
