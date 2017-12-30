package com.example.projekt.klientutveckling.firechat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ntn13dcm on 2017-12-15.
 */

public class CreateAccount extends ProgressActivity {

    private UserData userData = UserData.getUserData();
    private TextView userNameTextView;
    private EditText userNameTextField;
    private Button createDisplayname;
    private DatabaseReference mDatabase;
    private String currentUserId;
    private FirebaseAuth mAuth;
    private final List<User> userList = new ArrayList<User>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        userNameTextView = (TextView) findViewById(R.id.detail);
        userNameTextField = (EditText) findViewById(R.id.username);
        createDisplayname = (Button) findViewById(R.id.username_button);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        currentUserId = mAuth.getCurrentUser().getUid();

        createDisplayname.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                writeToDatabase();
            }
        });

        retrieveUserInfo();
    }

    private void retrieveUserInfo()
    {
        mDatabase.child("users").addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                Map<String, User> map = (Map<String, User>) dataSnapshot.getValue();
                userList.add(map.get("username"));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s)
            {
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
    }

    private void writeToDatabase()
    {
        final String username = userNameTextField.getText().toString();

        if(!TextUtils.isEmpty(username))
        {
            final String current_user = "users/" + currentUserId + "/";

            Map userInfoMap = new HashMap();
            userInfoMap.put("username", username);
            userInfoMap.put("email", mAuth.getCurrentUser().getEmail());

            Map userMap = new HashMap();
            userMap.put(current_user, userInfoMap);

            mDatabase.updateChildren(userMap, new DatabaseReference.CompletionListener()
            {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
                {

                    if(databaseError != null){

                        Log.d("CHAT_LOG", databaseError.getMessage().toString());

                    }
                }
            });

        }
        Intent intent = new Intent(CreateAccount.this, LobbyActivit.class);
        startActivity(intent);
    }

}
