package com.example.projekt.klientutveckling.firechat;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ofk14mbi on 2017-12-21.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>
{
    private List<Message> messagesList;
    private DatabaseReference dbRef;
    private FirebaseAuth mAuth;
    private String previousSender;

    public MessageAdapter(List<Message> messageList)
    {
        this.messagesList = messageList;
        dbRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        previousSender = "";
    }

    /**
     * Handles how messages are viewed. Messages by the sender(first if statement) are in a white bubble to the right. Other users to the left in purple.
     *
     * @param viewHolder
     * @param i
     */
    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i)
    {
        Message messages = messagesList.get(i);

        String currentUser = mAuth.getCurrentUser().getUid();
        String messageSender = messages.getFrom();

        if(messageSender.equals(currentUser)) //if we sent the message
        {
            viewHolder.messageViewFrom.setText(messages.getMessage());
            viewHolder.messageView.setVisibility(View.GONE);
            viewHolder.messageViewFrom.setVisibility(View.VISIBLE);
            viewHolder.messageView.setBottom(12);
            viewHolder.profileImageView.setVisibility(View.GONE);
            viewHolder.userInfo.setVisibility(View.GONE);
        } else //if someone else sent the message
        {
            viewHolder.messageView.setText(messages.getMessage());
            viewHolder.messageView.setVisibility(View.VISIBLE);
            viewHolder.messageViewFrom.setVisibility(View.GONE);
            viewHolder.messageView.setBottom(4);
            viewHolder.profileImageView.setVisibility(View.VISIBLE);

            if(!messageSender.equals(previousSender) && !previousSender.equals("")) {
                viewHolder.userInfo.setVisibility(View.VISIBLE);
                dbRef.child("users").child(messageSender).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String messageSenderDisplayname = dataSnapshot.getValue(String.class);
                        viewHolder.userInfo.setText(messageSenderDisplayname);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
        previousSender = messageSender;
    }

    @Override
    public int getItemCount()
    {
        return messagesList.size();
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_layout, parent, false);

        return new MessageViewHolder(v);
    }

    class MessageViewHolder extends RecyclerView.ViewHolder
    {
        public TextView messageView;
        public TextView messageViewFrom;
        public CircleImageView profileImageView;
        public TextView userInfo;

        public MessageViewHolder(View itemView)
        {
            super(itemView);

            messageView = (TextView) itemView.findViewById(R.id.chatTextView);
            messageViewFrom = (TextView) itemView.findViewById(R.id.chatTextFromView);
            profileImageView = (CircleImageView) itemView.findViewById(R.id.profileImageView);
            userInfo = (TextView) itemView.findViewById(R.id.userInfoBar);
        }
    }
}