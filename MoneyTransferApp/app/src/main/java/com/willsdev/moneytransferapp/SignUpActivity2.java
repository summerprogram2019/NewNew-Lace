package com.willsdev.moneytransferapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
        final String f_name = intent.getStringExtra("first_name");
        final String l_name = intent.getStringExtra("last_name");
        final String p_num = intent.getStringExtra("phone_number");
        final String country = intent.getStringExtra("country");

        final EditText username = findViewById(R.id.signup_username);
        final EditText password = findViewById(R.id.signup_password);
        final EditText pass_confirm = findViewById(R.id.signup_password_confirm);

        Button signup_finish = findViewById(R.id.splash_login);
        signup_finish.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(password.getText().toString().equals(pass_confirm.getText().toString())) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("user", username.getText().toString());
                    data.put("pass", password.getText().toString());
                    data.put("name", f_name);
                    //data.put("lname", l_name);
                    data.put("country", country);
                    data.put("language", "English");
                    data.put("pn", p_num);
                    RunQuery runQuery = new RunQuery(null, QueryType.SIGNUP, data);
                    NetworkThread networkThread = new NetworkThread(runQuery, SignUpActivity2.this);
                    networkThread.execute();
                } else {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Toast.makeText(getApplicationContext(), "Error: the two passwords must match", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }
}
