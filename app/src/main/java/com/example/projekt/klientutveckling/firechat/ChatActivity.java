package com.example.projekt.klientutveckling.firechat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupMenu;

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

public class ChatActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener
{
    private Toolbar mToolbar;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private RecyclerView mMessageList;
    private final List<Message> messageList = new ArrayList<>();
    private LinearLayoutManager mLinearLayoutManager;
    private MessageAdapter mMessageAdapter;

    private String currentUserID;
    private EditText mChatMessageView;
    private String mChatUser;

    private String messagesRoomName;
    private String roomName;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messagesRoomName = getIntent().getStringExtra("messagesRoomName");
        roomName = messagesRoomName;

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(roomName);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentUserID = mAuth.getCurrentUser().getUid();

        mChatUser = getIntent().getStringExtra("user_id"); //todo: is null because there's no putExtra() anywhere

        mChatMessageView = (EditText) findViewById(R.id.chat_message_view);
        mMessageList = (RecyclerView) findViewById(R.id.conv_list);

        mMessageAdapter = new MessageAdapter(messageList);

        mLinearLayoutManager = new LinearLayoutManager(this);
        mMessageList.setHasFixedSize(true);
        mMessageList.setLayoutManager(mLinearLayoutManager);
        mMessageList.setAdapter(mMessageAdapter);

        retrieveMessages();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); //pushar up layouten så att keyboard inte blockerar meddelanden

        mChatMessageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (mChatMessageView.getRight() - mChatMessageView.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        sendMessage();

                        return true;
                    }
                }
                return false;
            }
        });
    }

    /**
     * Inflate PopupMenu items
     * @param v
     */
    public void showPopup(View v)
    {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        popup.setOnMenuItemClickListener(this);
        inflater.inflate(R.menu.chat_menu_items, popup.getMenu());
        popup.show();
    }

    /**
     * Handles clicks on chat_menu_items
     * @param item
     * @return
     */
    @Override
    public boolean onMenuItemClick(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.add_action:
                Intent intent = new Intent(ChatActivity.this, AddUserActivity.class);
                intent.putExtra("messagesRoomName", messagesRoomName);
                intent.putExtra("roomName", roomName);
                startActivity(intent);
                finish();
                return true;
            default:
                return false;
        }
    }

    /**
     * Handles toolbar items. Adds back button that returns back to LobbyActivity
     * @param item
     * @return boolean
     */
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

    /**
     * Sends the text written in Textfield to database.
     * Database structure: message->
     *                              current_roomID + pushID ->
     *                                      message: string
     *                                      seen:    boolean
     *                                      time:    long
     *                                      from:    string
     */
    private void sendMessage()
    {
        final String message = mChatMessageView.getText().toString();

        if(!TextUtils.isEmpty(message)){

            final String current_room = "messages/" + messagesRoomName + "/"; //we're saying that current_room will be in the "messages" table

            DatabaseReference user_message_push = mDatabase.child("messages").child(messagesRoomName).push();

            String push_id = user_message_push.getKey();

            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", currentUserID);

            Map messageUserMap = new HashMap();
            messageUserMap.put(current_room + push_id, messageMap);

            mChatMessageView.setText("");

            mDatabase.child("latest").child(messagesRoomName).child("message").setValue(message);
            mDatabase.child("latest").child(messagesRoomName).child("time").setValue(ServerValue.TIMESTAMP);

            mDatabase.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    if(databaseError != null){

                        Log.d("CHAT_LOG", databaseError.getMessage().toString());
                    }

                }
            });

        }

    }

    private void retrieveMessages()
    {
        mDatabase.child("messages").child(messagesRoomName).addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                Message message = dataSnapshot.getValue(Message.class);
                messageList.add(message);
                mMessageAdapter.notifyDataSetChanged();
                mMessageList.scrollToPosition(mMessageAdapter.getItemCount()-1);
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
