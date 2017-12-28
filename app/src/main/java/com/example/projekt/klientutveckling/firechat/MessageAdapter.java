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
import com.google.firebase.database.DatabaseReference;

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

    public MessageAdapter(List<Message> messageList)
    {
        this.messagesList = messageList;
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i)
    {
        Message messages = messagesList.get(i);


        mAuth = FirebaseAuth.getInstance();
        String currentUser = mAuth.getCurrentUser().getUid();

        String messageSender = messages.getFrom();
        if(messageSender.equals(currentUser)) //if we sent the message
        {
            viewHolder.messageViewFrom.setText(messages.getMessage());
            viewHolder.messageView.setVisibility(View.GONE);
            viewHolder.messageViewFrom.setVisibility(View.VISIBLE);
            viewHolder.profileImageView.setVisibility(View.GONE);
        } else //if someone else sent the message
        {
            viewHolder.messageView.setText(messages.getMessage());
            viewHolder.messageView.setVisibility(View.VISIBLE);
            viewHolder.messageViewFrom.setVisibility(View.GONE);
            viewHolder.profileImageView.setVisibility(View.VISIBLE);
        }

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

        public MessageViewHolder(View itemView)
        {
            super(itemView);

            messageView = (TextView) itemView.findViewById(R.id.chatTextView);
            messageViewFrom = (TextView) itemView.findViewById(R.id.chatTextFromView);
            profileImageView = (CircleImageView) itemView.findViewById(R.id.profileImageView);
        }
    }
}