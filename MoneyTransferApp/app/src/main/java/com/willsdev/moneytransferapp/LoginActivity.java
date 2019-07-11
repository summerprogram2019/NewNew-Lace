package com.willsdev.moneytransferapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.ref.WeakReference;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class RunQuery {
    DBController dbController;
    QueryType queryType;
    Map<String, Object> data;

    public RunQuery(DBController dbController, QueryType queryType, Map<String,Object> data) {
        this.dbController = dbController;
        this.queryType = queryType;
        this.data = data;
    }
}

class Currency {
    String name;
    String code;
}

enum QueryType {
    LOGIN,GET_WALLETS,TRANSFER,SIGNUP,SETTINGS,RATES,CULTURES,WALLETS_LEFT,ADD_WALLET
}

/**
 * <Do in background,
 */
class NetworkThread extends AsyncTask<RunQuery, String, Map>
{
    private RunQuery runQuery;
    private WeakReference<Activity> activity;

    NetworkThread(RunQuery runQuery, Activity activity){
        this.runQuery=runQuery;
        this.activity = new WeakReference<>(activity);
    }

    protected Map doInBackground(RunQuery... arg0) {
        Map map = null;
        runQuery.dbController = ((MyApplication)activity.get().getApplication()).dbController;
        if(runQuery.dbController==null) {
            String ip = "192.168.43.98:3306";
            String host = "jdbc:mysql://"+ip+"/app?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
            String user = "app";
            String pass = "123123123App";
            runQuery.dbController = new DBController(host,user,pass);
            ((MyApplication)activity.get().getApplication()).dbController = runQuery.dbController;
        }
        final String s = "{" +
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
        switch (runQuery.queryType) {
            case LOGIN:{
                activity.get().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        activity.get().findViewById(R.id.login_progress).setVisibility(View.VISIBLE);
                    }
                });
                String user = (String) runQuery.data.get("user");
                String pass = (String) runQuery.data.get("pass");
                String login_query = "select * from users where login=\""+user+"\" and password=\"" + pass + "\"";
                try
                {
                    ResultSet result = runQuery.dbController.getData(login_query);
                    int count = 0;
                    int id = 0;
                    while (result.next()) {
                        id = result.getInt("user_id");
                        count++;
                    }
                    if(count>0){
                        activity.get().getSharedPreferences("userdetails", Context.MODE_PRIVATE).edit().putString("user",user).apply();
                        activity.get().getSharedPreferences("userdetails", Context.MODE_PRIVATE).edit().putString("pass",pass).apply();
                        activity.get().getSharedPreferences("userdetails", Context.MODE_PRIVATE).edit().putInt("user_id",id).apply();
                        Intent intent = new Intent(activity.get().getApplication().getApplicationContext(), MainActivity.class);
                        activity.get().startActivity(intent);
                    }
                } catch (Exception e)
                {
                    activity.get().runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Toast.makeText(activity.get().getApplicationContext(),"Login Failed. Please check your username and password",Toast.LENGTH_LONG).show();
                        }
                    });
                    e.printStackTrace();
                }
                activity.get().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        activity.get().findViewById(R.id.login_progress).setVisibility(View.INVISIBLE);
                    }
                });
                break;
            }
            case GET_WALLETS:{
                boolean popup = (boolean) runQuery.data.get("popup");
                int user_id = activity.get().getSharedPreferences("userdetails", Context.MODE_PRIVATE).getInt("user_id",0);
                try {
                    ResultSet currencies = runQuery.dbController.getData("select * from currencies");
                    Map<Integer, Currency> temp = new HashMap<>();
                    while (currencies.next()) {
                        Currency currency = new Currency();
                        currency.code = currencies.getString("code");
                        currency.name = currencies.getString("name");
                        temp.put(currencies.getInt("currency_id"),currency);
                    }
                    ResultSet rs = runQuery.dbController.getData("select * from accounts where users_user_id="+user_id);
                    List<Wallet> wallets = new ArrayList<>();
                    final Map<String, String> curr_symbols;

                    curr_symbols = new Gson().fromJson(s, new TypeToken<HashMap<String, String>>() {}.getType());
                    List<String> temp_s = new ArrayList<>();

                    while (rs.next()) {
                        int ccode = rs.getInt("currencies_currency_id");
                        String code = temp.get(ccode).code;
                        String country = temp.get(ccode).name;
                        double amount = rs.getDouble("amount");
                        String symbol = curr_symbols.get(code);
                        Wallet wallet = new Wallet(code,country,symbol,amount,ccode);
                        wallets.add(wallet);
                        temp_s.add(code);
                    }
                    if (popup) {
                        final ArrayAdapter<String> adapter = new ArrayAdapter<>(activity.get().getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, temp_s);
                        activity.get().runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                ((Spinner)runQuery.data.get("to_spinner")).setAdapter(adapter);
                                ((Spinner)runQuery.data.get("from_spinner")).setAdapter(adapter);
                            }
                        });

                    } else {

                        final MyCustomAdapter wallet_adapter = new MyCustomAdapter(wallets,activity.get());
                        activity.get().runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                ((ListView)runQuery.data.get("wallet_listview")).setAdapter(wallet_adapter);
                            }
                        });
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
                break;
            }
            case TRANSFER:{
                int user_id = activity.get().getSharedPreferences("userdetails",Context.MODE_PRIVATE).getInt("user_id",-1);
                String from_act = (String) runQuery.data.get("country_from");
                String to_act = (String) runQuery.data.get("country_to");
                double from_amt = (double) runQuery.data.get("from_amt");
                double to_amt = (double) runQuery.data.get("to_amt");

                try {
                    Map<String,Integer> currencies = new HashMap<>();
                    ResultSet rs = runQuery.dbController.getData("select * from currencies");
                    while (rs.next()) {
                        currencies.put(rs.getString("code"),rs.getInt("currency_id"));
                    }
                    double rate = (double) runQuery.data.get("rate");
                    runQuery.dbController.update(String.format("update accounts set amount=amount-%s where users_user_id=%s and currencies_currency_id=%s",from_amt,user_id,currencies.get(from_act)));
                    runQuery.dbController.update(String.format("update accounts set amount=amount+%s where users_user_id=%s and currencies_currency_id=%s",to_amt,user_id,currencies.get(to_act)));

                } catch ( Exception e) {
                    e.printStackTrace();
                }


                break;
            }
            case SIGNUP: {
                activity.get().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        activity.get().findViewById(R.id.signup_progress).setVisibility(View.VISIBLE);
                    }
                });
                Map<String, Object> data = runQuery.data;
                String user = (String) data.get("user");
                String pass = (String) data.get("pass");
                String name = (String) data.get("name");
                String pn = (String) data.get("pn");
                String country = (String) data.get("country");
                String language = (String) data.get("language");
                ResultSet rs;

                String check_avail = "select * from users where login=\""+user+"\"";
                try
                {
                    rs = runQuery.dbController.getData(check_avail);
                    int count = 0;
                    while (rs.next()) {
                        count++;
                    }
                    // check if user exists
                    if(count>0) {
                        activity.get().runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                Toast.makeText(activity.get().getApplicationContext(),
                                        "Error: username already taken. Please choose a different username",
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        // get country id and language id
                        country = runQuery.dbController.getData("select * from countries where name=\""+country+"\"").getString("country_id");
                        language = runQuery.dbController.getData("select *  from languages where name=\""+language+"\"").getString("language_id");
                        String query = String.format("insert into users values(\"%s\",\"%s\",\"%s\",%s,%s,%s)",
                                name,
                                user,
                                pass,
                                country,
                                language,
                                pn);
                        int status = runQuery.dbController.update(query);
                        if(status==0) {
                            activity.get().runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    Toast.makeText(activity.get().getApplicationContext(),
                                            "An error occured while creating your account. Try again later.",
                                            Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            String login_query = "select * from users where login=\""+user+"\" and password=\"" + pass + "\"";
                            ResultSet result = runQuery.dbController.getData(login_query);
                            int id = 0;
                            while (result.next()) {
                                id = result.getInt("user_id");
                            }
                            activity.get().getSharedPreferences("userdetails", Context.MODE_PRIVATE).edit().putString("user",user).apply();
                            activity.get().getSharedPreferences("userdetails", Context.MODE_PRIVATE).edit().putString("pass",pass).apply();
                            activity.get().getSharedPreferences("userdetails", Context.MODE_PRIVATE).edit().putInt("user_id",id).apply();
                            Intent intent = new Intent(activity.get().getApplication().getApplicationContext(), MainActivity.class);
                            activity.get().startActivity(intent);
                        }
                    }
                } catch (SQLException e)
                {
                    e.printStackTrace();
                }
                activity.get().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        activity.get().findViewById(R.id.signup_progress).setVisibility(View.INVISIBLE);
                    }
                });
                break;
            }
            case SETTINGS:{
                try
                {
                    int id = activity.get().getSharedPreferences("userdetails",Context.MODE_PRIVATE).getInt("user_id",-1);
                    if(id==-1) {

                    } else {
                        ResultSet rs = runQuery.dbController.getData("select * from users where user_id="+id);
                        String pn = rs.getString("phone_number");
                    }
                } catch (SQLException e)
                {
                    e.printStackTrace();
                }
                break;
            }
            case RATES: {
                try
                {
                    ResultSet rs = runQuery.dbController.getData("select * from currencies");
                    List<Rate> rates = new ArrayList<>();
                    Boolean popup = (Boolean) runQuery.data.get("popup");

                    final Map<String, String> curr_symbols;

                    curr_symbols = new Gson().fromJson(s, new TypeToken<HashMap<String, String>>() {}.getType());

                    while (rs.next()) {
                        float r = rs.getFloat("rate");
                        String ccode = rs.getString("code");
                        String symbol = curr_symbols.get(ccode);
                        String name = rs.getString("name");
                        rates.add(new Rate(r,ccode,symbol,name));
                    }

                    if (popup==null){
                        final RateListAdapter rateListAdapter = new RateListAdapter(rates,activity.get().getApplicationContext());
                        activity.get().runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                ((ListView)runQuery.data.get("rate_listview")).setAdapter(rateListAdapter);
                            }
                        });
                    } else {
                        Map<String,Rate> temp = new HashMap<>();
                        for (Rate r:
                             rates)
                        {
                            temp.put(r.currency_code,r);
                        }
                        ((MyApplication)activity.get().getApplication()).rates = temp;
                    }
                } catch (SQLException e)
                {
                    e.printStackTrace();
                }

                break;
            }
            case CULTURES: {
                try {
                    Map<Integer,String> codes = new HashMap<>();
                    ResultSet code_get = runQuery.dbController.getData("select * from currencies");
                    while (code_get.next()) {
                        codes.put(code_get.getInt("currency_id"),code_get.getString("code"));
                    }

                    ResultSet rs = runQuery.dbController.getData("select * from countries");
                    List<Culture> cultures = new ArrayList<>();

                    while (rs.next()){
                        String name = rs.getString("name");
                        String fin = rs.getString("general_info");
                        String taboos = rs.getString("taboos");
                        String ccode = codes.get(rs.getInt("currencies_currency_id"));
                        Culture culture = new Culture(name,ccode,taboos,fin);
                        cultures.add(culture);
                    }

                    final CultureListAdapter cultureListAdapter = new CultureListAdapter(cultures,activity.get().getApplicationContext());
                    activity.get().runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            ((ListView)runQuery.data.get("cultures_listview")).setAdapter(cultureListAdapter);
                        }
                    });
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            }
            case WALLETS_LEFT: {
                int user_id = (int) runQuery.data.get("user_id");
                try
                {
                    ResultSet rs = runQuery.dbController.getData("select * from currencies where currency_id not in (select currencies_currency_id from accounts where user_id="+user_id);
                    List<String> currencies = new ArrayList<>();
                    while (rs.next()) {
                        String code = rs.getString("code");
                        currencies.add(code);
                        System.out.println(code);
                    }
                    // Set Dropdown
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(activity.get().getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, currencies);
                    ((Spinner)runQuery.data.get("spinner")).setAdapter(adapter);
                } catch (SQLException e)
                {
                    e.printStackTrace();
                }
                break;
            }
            case ADD_WALLET: {
                String ccode = (String) runQuery.data.get("ccode");
                int currency_id = 0;
                try
                {
                    currency_id = runQuery.dbController.getData("select currency_id from currencies where code=\""+ccode+"\"").getInt("currency_id");
                    int user_id = activity.get().getSharedPreferences("userdetails", Context.MODE_PRIVATE).getInt("user_id",-1);
                    String query = String.format("insert into accounts values(%s,%s,%s)",0,user_id,currency_id);
                    int status = runQuery.dbController.update(query);
                    if(status==0) {
                        //bad
                    } else {
                        Intent intent = new Intent(activity.get().getApplication().getApplicationContext(), MainActivity.class);
                        activity.get().getApplicationContext().startActivity(intent);
                    }
                } catch (SQLException e)
                {
                    e.printStackTrace();
                }
                break;
            }
        }
        return map;
    }
}

public class LoginActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText login = findViewById(R.id.login_user);
        final EditText pass = findViewById(R.id.login_pass);

        Button login_btn = findViewById(R.id.btn_login1);
        login_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Map<String,Object> data = new HashMap<>();
                data.put("user",login.getText().toString());
                data.put("pass",pass.getText().toString());
                final RunQuery runQuery = new RunQuery(null, QueryType.LOGIN, data);
                NetworkThread networkThread = new NetworkThread(runQuery, LoginActivity.this);
                networkThread.execute(runQuery);
            }
        });
    }
}
