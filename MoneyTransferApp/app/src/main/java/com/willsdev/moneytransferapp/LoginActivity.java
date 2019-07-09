package com.willsdev.moneytransferapp;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
    LOGIN,GET_WALLETS,TRANSFER,SIGNUP
}

/**
 * <Do in background,
 */
class NetworkThread extends AsyncTask<RunQuery, String, Map>
{
    private RunQuery runQuery;
    private Activity activity;

    NetworkThread(RunQuery runQuery, Activity activity){
        this.runQuery=runQuery;
        this.activity = activity;
    }

    protected Map doInBackground(RunQuery... arg0) {
        Map map = null;
        runQuery.dbController = ((MyApplication)activity.getApplication()).dbController;
        if(runQuery.dbController==null) {
            String ip = "192.168.43.98:3306";
            String host = "jdbc:mysql://"+ip+"/app?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
            String user = "app";
            String pass = "123123123App";
            runQuery.dbController = new DBController(host,user,pass);
            ((MyApplication)activity.getApplication()).dbController = runQuery.dbController;
        }
        switch (runQuery.queryType) {
            case LOGIN:{
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
                        activity.getSharedPreferences("userdetails", Context.MODE_PRIVATE).edit().putString("user",user).apply();
                        activity.getSharedPreferences("userdetails", Context.MODE_PRIVATE).edit().putString("pass",pass).apply();
                        activity.getSharedPreferences("userdetails", Context.MODE_PRIVATE).edit().putInt("user_id",id).apply();
                        Intent intent = new Intent(activity.getApplication().getApplicationContext(), MainActivity.class);
                        activity.startActivity(intent);
                    }
                } catch (Exception e)
                {
                    activity.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Toast.makeText(activity.getApplicationContext(),"Login Failed. Please check your username and password",Toast.LENGTH_LONG).show();
                        }
                    });
                    e.printStackTrace();
                }
                break;
            }
            case GET_WALLETS:{
                int user_id = activity.getSharedPreferences("userdetails", Context.MODE_PRIVATE).getInt("user_id",0);
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

                    while (rs.next()) {
                        int ccode = rs.getInt("currencies_currency_id");
                        String code = temp.get(ccode).code;
                        String country = temp.get(ccode).name;
                        double amount = rs.getDouble("amount");
                        String symbol = curr_symbols.get(code);
                        Wallet wallet = new Wallet(code,country,symbol,amount);
                        wallets.add(wallet);
                    }
                    final MyCustomAdapter wallet_adapter = new MyCustomAdapter(wallets,activity.getApplicationContext());
                    activity.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            ((ListView)runQuery.data.get("wallet_listview")).setAdapter(wallet_adapter);
                        }
                    });
                } catch (Exception e){
                    e.printStackTrace();
                }
                break;
            }
            case TRANSFER:{
                String user_id = (String) runQuery.data.get("user_id");
                String from_act = (String) runQuery.data.get("initial_amt");
                String to_act = (String) runQuery.data.get("final_amt");
                int transfer_from = runQuery.dbController.update(String.format("UPDATE accounts set amount=%s where users_user_id=%s and currencies_currency_id=10",from_act,user_id));
                int transfer_to = runQuery.dbController.update(String.format("UPDATE accounts set amount=%s where users_user_id=%s and currencies_currency_id=10",to_act,user_id));
                if(transfer_from+transfer_to!=2){
                    try
                    {
                        throw new Exception("Transfer failed");
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                break;
            }
            case SIGNUP:{
                Map<String, Object> data = runQuery.data;
                String user = (String) data.get("user");
                String pass = (String) data.get("pass");
                String name = (String) data.get("name");
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
                        activity.runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                Toast.makeText(activity.getApplicationContext(),
                                        "Error: username already taken. Please choose a different username",
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        String query = String.format("insert into users values(\"%s\",\"%s\",\"%s\",%s,%s)",
                                name,
                                user,
                                pass,
                                country,
                                language);
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

public class LoginActivity extends AppCompatActivity
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
