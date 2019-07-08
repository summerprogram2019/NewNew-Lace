package com.willsdev.moneytransferapp;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.junit.Test;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest
{
    @Test
    public void get_exchange_rates() throws IOException
    {
        Map<String, String> curr_symbols;
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
        DecimalFormat df = new DecimalFormat("0.00");

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .build();

        String currency = "USD";

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

        Response response = okHttpClient.newCall(request).execute();
        String content = response.body().string();
        JsonObject json = (new JsonParser().parse(content).getAsJsonObject());
        JsonObject rates = json.get("rates").getAsJsonObject();
        Map<String, Float> map = new Gson().fromJson(rates, new TypeToken<TreeMap<String, Float>>() {}.getType());

        for (Map.Entry<String, Float> e: map.entrySet())
        {
            String symbol = curr_symbols.get(e.getKey());
            if (symbol == null) symbol = e.getKey();
            System.out.println(e.getKey() + " : " + symbol + df.format(e.getValue()));
        }

        //MyCustomAdapter myCustomAdapter = new MyCustomAdapter(map, getApplicationContext());
    }

    @Test
    public void testConnect() throws SQLException
    {
        String host = "jdbc:mysql://91.132.103.123:3306/app?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        String user = "root";
        String pass = "123123123App";
        DBController controller = new DBController(host, user, pass);

//        int resultSet = controller.update("INSERT INTO accounts VALUES(0,1,11)");
//        System.out.println(resultSet);
//
//        int resultSet = controller.update("INSERT INTO currencies VALUES(11,\"China\",\"CNY\")");
//        System.out.println(resultSet);
//        ResultSet rs = controller.getData("SELECT * from currencies");
////        ResultSet resultSet = controller.getData("INSERT INTO accounts VALUES (100, 1, 1.75, \"2006-02-02 15:35:00\" )");
//        while (rs.next()) {
//            System.out.println(rs.getInt("currency_id"));
//            System.out.println(rs.getString("name"));
//            System.out.println(rs.getString("code"));
//        }
        //update AUD
        int results = controller.update("UPDATE accounts set amount=50 where users_user_id=1 and currencies_currency_id=10");
        // update CNY
        controller.update("UPDATE accounts set amount=350 where users_user_id=1 and currencies_currency_id=11");
        ResultSet rs = controller.getData("SELECT * from accounts");
        while (rs.next()) {
            System.out.println(rs.getString("amount"));
            System.out.println(rs.getString("users_user_id"));
            System.out.println(rs.getString("currencies_currency_id"));
        }
    }
}