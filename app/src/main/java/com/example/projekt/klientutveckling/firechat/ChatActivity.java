package com.example.projekt.klientutveckling.firechat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.ImageButton;
import android.widget.PopupMenu;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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
    private StorageReference mImageStorage;

    private RecyclerView mMessageList;
    private final List<Message> messageList = new ArrayList<>();
    private LinearLayoutManager mLinearLayoutManager;
    private MessageAdapter mMessageAdapter;

    private String currentUserID;
    private EditText mChatMessageView;
    private String mChatUser;

    private ImageButton mSendImageButton;

    private String messagesRoomName;
    private String roomName;

    private final int PICK_IMAGE = 1;

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
        mImageStorage = FirebaseStorage.getInstance().getReference();
        currentUserID = mAuth.getCurrentUser().getUid();

        mChatUser = getIntent().getStringExtra("user_id"); //todo: is null because there's no putExtra() anywhere

        mChatMessageView = (EditText) findViewById(R.id.chat_message_view);
        mMessageList = (RecyclerView) findViewById(R.id.conv_list);
        mSendImageButton = (ImageButton) findViewById(R.id.send_image_button);

        mMessageAdapter = new MessageAdapter(messageList);

        mLinearLayoutManager = new LinearLayoutManager(this);
        mMessageList.setHasFixedSize(true);
        mMessageList.setLayoutManager(mLinearLayoutManager);
        mMessageList.setAdapter(mMessageAdapter);

        retrieveMessages();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); //pushar up layouten så att keyboard inte blockerar meddelanden

        //Eftersom "send message" knappen ligger i själva EditText så måste vi hämta den som "drawable right" för att kunna känna av att knappen har klickats.
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

        mSendImageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE);
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
            messageMap.put("type", "text");

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK){

            Uri imageUri = data.getData();

            final String current_room = "messages/" + messagesRoomName + "/"; //we're saying that current_room will be in the "messages" table

            DatabaseReference user_message_push = mDatabase.child("messages").child(messagesRoomName).push();

            final String push_id = user_message_push.getKey();

            StorageReference filepath = mImageStorage.child("message_images").child( push_id + ".jpg");

            filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if(task.isSuccessful()){

                        String image_url = task.getResult().getDownloadUrl().toString();


                        Map messageMap = new HashMap();
                        messageMap.put("message", image_url);
                        messageMap.put("seen", false);
                        messageMap.put("time", ServerValue.TIMESTAMP);
                        messageMap.put("from", currentUserID);
                        messageMap.put("type", "image");

                        Map messageUserMap = new HashMap();
                        messageUserMap.put(current_room + push_id, messageMap);


                        mDatabase.child("latest").child(messagesRoomName).child("message").setValue(image_url);
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
            });

        }


    }
}
