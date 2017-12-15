package com.example.projekt.klientutveckling.firechat;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by ntn13dcm on 2017-12-15.
 */

public class CreateAccount extends Activity{

    private TextView userNameTextView;
    private EditText userNameTextField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userNameTextView = (TextView) findViewById(R.id.detail);
        userNameTextField = (EditText) findViewById(R.id.field_email);

        //findViewById(R.id.username_button).setOnClickListener(this);

    }

    public void onClick(View view)
    {
        int i = view.getId();
        if (i == R.id.username_button) {


        }
    }

}
