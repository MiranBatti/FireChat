package com.example.projekt.klientutveckling.firechat;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by ofk14den on 2017-12-18.
 */

public class LobbyActivit extends Activity{

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref = database.getReference("Users");

    private ListView lv;
    private List oldChatsList;
    private Map<String,Objects> td;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        creatList();
       // printList();
    }


    private void creatList() {
    ref.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
           td = (HashMap<String,Objects>) dataSnapshot.getValue();

            List<Objects> oldChatsList = new ArrayList<>(td.values());

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    });


    }
     private void printList(){

         lv = (ListView) findViewById(R.id.lobbyList);

         // Instanciating an array list (you don't need to do this,
         // you already have yours).


         // This is the array adapter, it takes the context of the activity as a
         // first parameter, the type of list view as a second parameter and your
         // array as a third parameter.
         ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                 this,
                 android.R.layout.simple_list_item_1,
                 oldChatsList);

         lv.setAdapter(arrayAdapter);
     }
    private void addEvent(String string ){

    }
}
