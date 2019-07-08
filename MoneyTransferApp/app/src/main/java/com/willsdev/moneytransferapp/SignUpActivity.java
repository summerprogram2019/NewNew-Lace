package com.willsdev.moneytransferapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity
{
    RunQuery runQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);



        //Button signup = findViewById(R.id.btn_signup)
//        String user_entry = ((EditText)findViewById(R.id.sign_up_user_entry)).getText().toString();
//        String pass_entry = ((EditText)findViewById(R.id.signup_pass_entry)).getText().toString();
//        String name_entry = ((EditText)findViewById(R.id.signup_name_entry)).getText().toString();
//        String country_id = ((EditText)findViewById(R.id.signup_country_id)).getText().toString();
//        String language_id = ((EditText)findViewById(R.id.signup_language_id)).getText().toString();

        Map<String, String> data = new HashMap<>();
//        data.put("user",user_entry);
//        data.put("pass",pass_entry);
//        data.put("name",name_entry);
//        data.put("country",country_id);
//        data.put("language",language_id);
        runQuery = new RunQuery(null, QueryType.SIGNUP,data);

        NetworkThread networkThread = new NetworkThread(runQuery, this);
        networkThread.execute();

    }
}
