package com.willsdev.moneytransferapp;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
                    if (checked_rates) {
                        RatesFragment transferFragment = new RatesFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("rates", ser);
                        transferFragment.setArguments(bundle);
                        openFragment(transferFragment);
                    } else {
                        final Map<String, String> curr_symbols;
                        String curr_symbols_json = "{" +
                                "AUD: $" + "," +
                                "BGN: лв" + "," +
                                "BRL: R$" + "," +
                                "CAD: $" + "," +
                                "CHF: CHF" + "," +
                                "CNY: ¥" + "," +
                                "CZK: Kč" + "," +
                                "DKK: kr" + "," +
                                "EUR: €" + "," +
                                "GBP: £" + "," +
                                "HKD: $" + "," +
                                "HRK: kn" + "," +
                                "HUF: ft" + "," +
                                "IDR: Rp" + "," +
                                "ILS: ₪" + "," +
                                "INR: R" + "," +
                                "ISK: kr" + "," +
                                "JPY: ¥" + "," +
                                "KRW: ₩" + "," +
                                "MXN: $" + "," +
                                "MYR: RM" + "," +
                                "NOK: kr" + "," +
                                "NZD: $" + "," +
                                "PHP: ₱" + "," +
                                "PLN: zł" + "," +
                                "RON: lei" + "," +
                                "RUB: \u20BD" + "," +
                                "SEK: kr" + "," +
                                "SGD: $" + "," +
                                "THB: ฿" + "," +
                                "TRY: TRY" + "," +
                                "USD: $" + "," +
                                "ZAR: R" +
                                "}";

                        curr_symbols = new Gson().fromJson(curr_symbols_json, new TypeToken<HashMap<String, String>>() {}.getType());
                        final DecimalFormat df = new DecimalFormat("0.00");

                        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                                .build();

                        String currency = "AUD";

                        HttpUrl url = new HttpUrl.Builder()
                                .scheme("https")
                                .host("api.exchangeratesapi.io")
                                .addPathSegments("latest")
                                .addQueryParameter("base", currency)
                                .build();

                        //init request
                        Request request= new Request.Builder()
                                .url(url)
                                .build();

                        okHttpClient.newCall(request).enqueue(new Callback()
                        {
                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e)
                            {

                            }

                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException
                            {
                                String content = response.body().string();
                                JsonObject json = (new JsonParser().parse(content).getAsJsonObject());
                                JsonObject rates_json = json.get("rates").getAsJsonObject();
                                Map<String, Float> map = new Gson().fromJson(rates_json, new TypeToken<TreeMap<String, Float>>() {}.getType());

                                for (Map.Entry<String, Float> e: map.entrySet())
                                {
                                    String symbol = curr_symbols.get(e.getKey());
                                    String country_name = e.getKey();
                                    String currency_code = e.getKey();
                                    float rate = e.getValue();
                                    Rate temp = new Rate(rate, currency_code, symbol, country_name);
                                    rates.add(temp);
                                }

                                runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        toolbar.setTitle("Rates");
                                    }
                                });

                                RatesFragment transferFragment = new RatesFragment();
                                Bundle bundle = new Bundle();
                                ser = serialise(rates);
                                bundle.putString("rates", ser);
                                transferFragment.setArguments(bundle);
                                checked_rates = true;
                                openFragment(transferFragment);
                            }
                        });
                    }
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

    /**
     *
     * @param context   App context for the toast
     * @param msg       Message to display
     * @param length    SHORT = 0, LONG = 1
     */
    public static void toastMsg(Context context, String msg, int length) {
        Toast.makeText(context, msg, length).show();
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
