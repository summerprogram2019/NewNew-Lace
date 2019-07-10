package com.willsdev.moneytransferapp;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.junit.Test;

import java.io.IOException;
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

        Response response = okHttpClient.newCall(request).execute();
        String content = response.body().string();
        JsonObject json = (new JsonParser().parse(content).getAsJsonObject());
        JsonObject rates = json.get("rates").getAsJsonObject();
        Map<String, Float> map = new Gson().fromJson(rates, new TypeToken<TreeMap<String, Float>>() {}.getType());

        for (Map.Entry<String, Float> e: map.entrySet())
        {
            String symbol = curr_symbols.get(e.getKey());
            if (symbol == null) symbol = e.getKey();
            System.out.println(e.getKey() + " : " + df.format(e.getValue()));
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

        String muser = "NikSavilov";
        String mpass = "123123123";
        String login_query = "select * from users where login=\""+muser+"\" and password=\"" + mpass + "\"";
        ResultSet rs = controller.getData(login_query);
        while (rs.next()) {
            System.out.println(rs.getString("name"));
        }
    }
}