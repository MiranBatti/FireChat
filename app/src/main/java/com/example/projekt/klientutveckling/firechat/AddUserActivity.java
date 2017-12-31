package com.example.projekt.klientutveckling.firechat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
 * Created by Miran on 31/12/2017.
 */

public class AddUserActivity extends AppCompatActivity
{
    private Toolbar mToolbar;
    private ImageButton addButton;
    private ListView userListView;
    private DatabaseReference mDatabase;
    private final List<String> usernameList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private String room;
    private Map<String, String> userIdMap;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_user);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        addButton = (ImageButton) findViewById(R.id.addButton);
        userListView = (ListView) findViewById(R.id.user_list);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        room = getIntent().getStringExtra("room");
        userIdMap = new HashMap<>();

        addButton.setVisibility(View.GONE);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Pick user to add");

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                usernameList);
        userListView.setAdapter(adapter);
        retrieveUserInfo();

        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                String item = userIdMap.get (((TextView)view).getText().toString());

                addUserToRoom(item, i);

                Toast.makeText(getBaseContext(), item, Toast.LENGTH_LONG).show();
            }
        });

    }

    private void addUserToRoom(String item, int i)
    {
        Map roomMap = new HashMap();
        roomMap.put(room,"test");
        mDatabase.child("users").child(item).child("rooms").updateChildren(roomMap);
        usernameList.remove(i);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(AddUserActivity.this, ChatActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void retrieveUserInfo()
    {
        mDatabase.child("users").addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                User user = dataSnapshot.getValue(User.class);
                String username = user.getUsername();

                if(user.roomExist())
                {
                    for (Map.Entry<String, String> entry: user.getRooms().entrySet())
                    {
                        if(entry.getKey().equals(room))
                        {
                            username = "";
                        }
                    }
                }

                if(!username.equals(""))
                {
                    userIdMap.put(username, dataSnapshot.getKey());
                    usernameList.add(username);
                    adapter.notifyDataSetChanged();
                }
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
