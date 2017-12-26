package com.example.projekt.klientutveckling.firechat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Miran on 12/12/2017.
 */

public class ChatActivity extends AppCompatActivity
{
    private Toolbar mToolbar;

    private FirebaseAuth mAuth;
    private String currentUserID;
    private EditText mChatMessageView;
    private String mChatUser;
    private DatabaseReference dbRef;
    private ImageButton mSendBtn;
    private RecyclerView mMessageList;
    private final List<Message> messageList = new ArrayList<>();
    private LinearLayoutManager mLinearLayoutManager;
    private MessageAdapter mMessageAdapter;

    private String roomName;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Room Name");

        roomName = "NewRoom"; //TODO: room name should be passed from lobby. hard coded, for now

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();
        currentUserID = mAuth.getCurrentUser().getUid();

        mChatUser = getIntent().getStringExtra("user_id"); //todo: is null because there's no putExtra() anywhere
        mSendBtn = (ImageButton) findViewById(R.id.chat_send_btn);
        mChatMessageView = (EditText) findViewById(R.id.chat_message_view);
        mMessageList = (RecyclerView) findViewById(R.id.conv_list);

        mMessageAdapter = new MessageAdapter(messageList);

        mLinearLayoutManager = new LinearLayoutManager(this);
        mMessageList.setHasFixedSize(true);
        mMessageList.setLayoutManager(mLinearLayoutManager);
        mMessageList.setAdapter(mMessageAdapter);

        loadMessages();

        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(ChatActivity.this, LobbyActivit.class); //Skickar tillbaka till LobbyActivity när "back" pilen på toolbar klickas
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void sendMessage()
    {
        String message = mChatMessageView.getText().toString();

        if(!TextUtils.isEmpty(message)){

            String current_user_ref = "messages/" + currentUserID + "/" + mChatUser;
            String chat_user_ref = "messages/" + mChatUser + "/" + current_user_ref;

            String current_user = "messages/" + currentUserID;


            DatabaseReference user_message_push = dbRef.child("Rooms")
                    .child("Room1").push(); //TODO: Room1 bör ersättas med en variabel med nuvarande rum.

            String push_id = user_message_push.getKey();

            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", currentUserID);

            Map messageUserMap = new HashMap();
            //messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
            //messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);
            messageUserMap.put(current_user + push_id, messageMap);

            Map roomMap = new HashMap();
            roomMap.put(roomName, messageUserMap);

            Map mainRoomsMap = new HashMap();
            mainRoomsMap.put("NewRooms", roomMap);

            mChatMessageView.setText("");

            /*
            dbRef.child("Rooms").child("Room1").child("timestamp").setValue(ServerValue.TIMESTAMP);
            dbRef.child("Rooms").child("Room1").child("message").setValue(message);
            */

            dbRef.updateChildren(mainRoomsMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    if(databaseError != null){

                        Log.d("CHAT_LOG", databaseError.getMessage().toString());

                    }

                }
            });

        }

    }

    private void loadMessages()
    {
        dbRef.child("Rooms").addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                Message message = dataSnapshot.getValue(Message.class);
                messageList.add(message);
                mMessageAdapter.notifyDataSetChanged();
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
}
