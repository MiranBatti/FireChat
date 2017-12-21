package com.example.projekt.klientutveckling.firechat;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ofk14mbi on 2017-12-15.
 */

public class ChatFragment extends Fragment
{
    private FirebaseAuth mAuth;
    private String currentUserID;
    private EditText mChatMessageView;
    private String mChatUser;
    private DatabaseReference dbRef;
    private ImageButton mSendBtn;
    private MessageAdapter mMessageAdapter;
    private RecyclerView mMessageList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);


        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();
        currentUserID = mAuth.getCurrentUser().getUid();

        mChatUser = getActivity().getIntent().getStringExtra("user_id");
        mSendBtn = (ImageButton) rootView.findViewById(R.id.chat_send_btn);
        mChatMessageView = (EditText) rootView.findViewById(R.id.chat_message_view);

        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        return rootView;
    }

    private void sendMessage()
    {
        String message = mChatMessageView.getText().toString();

        if(!TextUtils.isEmpty(message)){

            String current_user_ref = "messages/" + currentUserID + "/" + mChatUser;
            String chat_user_ref = "messages/" + mChatUser + "/" + current_user_ref;

            DatabaseReference user_message_push = dbRef.child("Rooms")
                    .child("Room1").push(); //TODO: Room1 bör ersättas med en variabel med nuvarande rum.

            String push_id = user_message_push.getKey();

            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", currentUserID);

            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
            messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

            mChatMessageView.setText("");

            dbRef.child("Rooms").child("Room1").child("timestamp").setValue(ServerValue.TIMESTAMP);
            dbRef.child("Rooms").child("Room1").child("message").setValue(message);

            dbRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    if(databaseError != null){

                        Log.d("CHAT_LOG", databaseError.getMessage().toString());

                    }

                }
            });

        }

    }
}
