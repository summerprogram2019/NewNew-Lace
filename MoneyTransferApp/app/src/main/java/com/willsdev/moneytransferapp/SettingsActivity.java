package com.willsdev.moneytransferapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setTitle("Settings");

        Map<String,Object> data = new HashMap<>();
        RunQuery query = new RunQuery(null,QueryType.SETTINGS,data);
        NetworkThread networkThread = new NetworkThread(query,this);
        networkThread.execute();

        Button logout = findViewById(R.id.btn_logout);
        logout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getSharedPreferences("userdetails",MODE_PRIVATE).edit().clear().apply();
                Intent intent = new Intent(SettingsActivity.this,SplashScreenActivity.class);
                startActivity(intent);
            }
        });
    }
}
