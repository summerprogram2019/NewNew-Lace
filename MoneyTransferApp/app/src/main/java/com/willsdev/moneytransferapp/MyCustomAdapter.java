package com.willsdev.moneytransferapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.willsdev.moneytransferapp.MainActivity.getFlagResource;


class Wallet {
    String currency_code;
    String country;
    String symbol;
    double balance;
    int id;

    public Wallet(String currency_code, String country, String symbol, double balance, int id) {
        this.currency_code = currency_code;
        this.country = country;
        this.symbol = symbol;
        this.balance = balance;
        this.id=id;
    }
}

public class MyCustomAdapter extends BaseAdapter implements ListAdapter
{
    private Context context;
    private List<Wallet> list;
    DecimalFormat df = new DecimalFormat("0.00");

    public MyCustomAdapter(List<Wallet> list, Context context)
    {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount()
    {
        return list.size();
    }

    @Override
    public Object getItem(int pos)
    {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos)
    {
        return 0;
        //just return 0 if your list items do not have an Id variable.
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        View view = convertView;
        if (view == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listview_template, null);
        }

        final Wallet curr_wallet = list.get(position);

        TextView amount = view.findViewById(R.id.wallet_amount);
        amount.setText(curr_wallet.symbol + df.format(curr_wallet.balance));

        TextView country_name = view.findViewById(R.id.wallet_country_name);
        country_name.setText(curr_wallet.country);

        ImageView flag_img = view.findViewById(R.id.wallet_country_flag);
        flag_img.setImageDrawable(getFlagResource(curr_wallet.currency_code, context));

        LinearLayout container = view.findViewById(R.id.wallet_container);
        container.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(context,TransferActivity.class);
                intent.putExtra("selected",serialise(curr_wallet));
            }
        });

        return view;
    }

    public String serialise(Wallet users) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream so = new ObjectOutputStream(bo);
            so.writeObject(users);
            so.flush();
            return new String(Base64.encode(bo.toByteArray(), Base64.DEFAULT));
        } catch (Exception e) {

        }
        return "";
    }
}