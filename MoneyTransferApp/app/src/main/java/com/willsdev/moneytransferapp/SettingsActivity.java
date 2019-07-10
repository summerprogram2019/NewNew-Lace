package com.willsdev.moneytransferapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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
    }
}
