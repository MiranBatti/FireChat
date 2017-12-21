package com.example.projekt.klientutveckling.firechat;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

    public MessageAdapter(List<Message> messageList)
    {
        this.messagesList = messageList;
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i)
    {
        Message messages = messagesList.get(i);
        viewHolder.messageView.setText(messages.getMessage());
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
        public CircleImageView profileImageView;

        public MessageViewHolder(View itemView)
        {
            super(itemView);

            messageView = (TextView) itemView.findViewById(R.id.chatTextView);
            profileImageView = (CircleImageView) itemView.findViewById(R.id.profileImageView);
        }
    }
}