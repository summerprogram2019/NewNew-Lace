package com.willsdev.moneytransferapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

public class TransferActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);
    }

    public Wallet deserialise (String s) {
        try {
            byte[] b = Base64.decode(s.getBytes(), Base64.DEFAULT);
            ByteArrayInputStream bi = new ByteArrayInputStream(b);
            ObjectInputStream si = new ObjectInputStream(bi);
            return (Wallet) si.readObject();
        } catch (Exception e) {
        }
        return null;
    }
}
