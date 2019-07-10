package com.willsdev.moneytransferapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    BottomNavigationView bottomNavigationView;
    ActionBar toolbar;
    Context context;
    boolean checked_rates;
    List<Rate> rates;
    String ser;

    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener()
    {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
        {
            switch (menuItem.getItemId()) {
                case R.id.navigation_wallet: {
                    toolbar.setTitle("Wallet");
                    WalletFragment walletFragment = new WalletFragment();
                    openFragment(walletFragment);
                    return true;
                }
                case R.id.navigation_transfer: {
                    toolbar.setTitle("Rates");
                    RatesFragment ratesFragment = new RatesFragment();
                    openFragment(ratesFragment);
                    return true;
                }
                case R.id.navigation_culture: {
                    toolbar.setTitle("Culture");
                    CultureFragment cultureFragment = new CultureFragment();
                    openFragment(cultureFragment);
                    return true;
                }
            }
            return false;
        }
    };

    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void settings_click(MenuItem item) {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();
        toolbar = getSupportActionBar();
        rates = new ArrayList<>();
        checked_rates = false;

        bottomNavigationView = findViewById(R.id.navigation_view);

        /* Set a listener that will be notified when a bottom navigation item is selected. */
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);



        /* Start With Wallets */
        toolbar.setTitle("Wallet");
        WalletFragment walletFragment = new WalletFragment();
        openFragment(walletFragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_actions, menu);

        return super.onCreateOptionsMenu(menu);
    }

    public static String serialise(List<Rate> users) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream so = new ObjectOutputStream(bo);
            so.writeObject(users);
            so.flush();
            return new String(Base64.encode(bo.toByteArray(), Base64.DEFAULT));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static Drawable getFlagResource(String code, Context context) {
        Resources resources = context.getResources();
        final int resourceId = resources.getIdentifier("f_"+code.toLowerCase(), "drawable",
                context.getPackageName());
        try {
            return resources.getDrawable(resourceId);
        } catch (Resources.NotFoundException e) {
            return  null;
        }
    }
}
