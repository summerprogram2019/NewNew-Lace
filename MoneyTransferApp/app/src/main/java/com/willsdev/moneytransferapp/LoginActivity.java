package com.willsdev.moneytransferapp;

import android.app.Activity;
import android.app.Application;
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
    Map<String, String> data;

    public RunQuery(DBController dbController, QueryType queryType, Map<String,String> data) {
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
        if(runQuery.dbController==null) {
            String host = "jdbc:mysql://91.132.103.123:3306/app?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
            String user = "root";
            String pass = "123123123App";
            runQuery.dbController = new DBController(host,user,pass);
            ((MyApplication)activity.getApplication()).dbController = runQuery.dbController;
        }
        switch (runQuery.queryType) {
            case LOGIN:{
                String login_query = "select * from users where login=\""+runQuery.data.get("user")+"\" and pass=\"" + runQuery.data.get("pass") + "\"";
                try
                {
                    int result = runQuery.dbController.getData(login_query).getMetaData().getColumnCount();
                    if(result>0){
                        Intent intent = new Intent(activity.getApplication().getApplicationContext(), MainActivity.class);
                        activity.startActivity(intent);
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            case GET_WALLETS:{
                String user_id = runQuery.data.get("user_id");
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
                    ListView sv = activity.findViewById(R.id.wallet_scroll);
                    MyCustomAdapter wallet_adapter = new MyCustomAdapter(wallets,activity.getApplicationContext());
                    sv.setAdapter(wallet_adapter);
                } catch (Exception e){}
            }
            case TRANSFER:{
                String user_id = runQuery.data.get("user_id");
                String from_act = runQuery.data.get("initial_amt");
                String to_act = runQuery.data.get("final_amt");
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
            }
            case SIGNUP:{
                Map<String, String> data = runQuery.data;
                String user = data.get("user");
                String pass = data.get("pass");
                String name = data.get("name");
                String country = data.get("country");
                String language = data.get("language");
                String query = "";
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

        getApplication();

        String login = ((EditText)findViewById(R.id.login_user)).getText().toString();
        String pass = ((EditText)findViewById(R.id.login_pass)).getText().toString();
        Map<String,String> data = new HashMap<>();
        data.put("user",login);
        data.put("pass",pass);
        final RunQuery runQuery = new RunQuery(null, QueryType.LOGIN, data);

        Button login_btn = findViewById(R.id.btn_login);
        login_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                NetworkThread networkThread = new NetworkThread(runQuery, LoginActivity.this);
                networkThread.execute(runQuery);
            }
        });

        Button signup = findViewById(R.id.btn_signup);
        signup.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }
}
