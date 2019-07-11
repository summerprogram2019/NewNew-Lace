package com.willsdev.moneytransferapp;

import android.app.Application;

import java.util.Map;

public class MyApplication extends Application
{
    public DBController dbController;
    public Map<String,Rate> rates;
}
