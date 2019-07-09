package com.willsdev.moneytransferapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity2 extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up1);

        Intent intent = getIntent();
        String f_name = intent.getStringExtra("first_name");
        String l_name = intent.getStringExtra("last_name");
        String p_num = intent.getStringExtra("phone_number");
        String country = intent.getStringExtra("country");

        EditText username = findViewById(R.id.signup_username);
        EditText password = findViewById(R.id.signup_password);
        EditText pass_confirm = findViewById(R.id.signup_password_confirm);

        Button signup_finish = findViewById(R.id.splash_login);
        signup_finish.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Map<String, Object> data = new HashMap<>();
                //data.put("f_name")
                RunQuery runQuery = new RunQuery(null, QueryType.SIGNUP, data);
                //NetworkThread networkThread = new NetworkThread()
            }
        });
    }
}
