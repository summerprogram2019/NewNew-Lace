package com.willsdev.moneytransferapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.List;

import static com.willsdev.moneytransferapp.MainActivity.getFlagResource;


class Rate implements Serializable
{
    public float rate;
    public String currency_code;
    public String currency_symbol;
    public String country_name;

    public Rate(float rate, String  currency_code, String currency_symbol, String country_name) {
        this.rate = rate;
        this.currency_code = currency_code;
        this.currency_symbol = currency_symbol;
        this.country_name = country_name;
    }
}

public class RateListAdapter extends BaseAdapter implements ListAdapter
{
    private Context context;
    private List<Rate> list;
    private DecimalFormat df = new DecimalFormat("0.00");

    public RateListAdapter(List<Rate> list, Context context) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount()
    {
        return list.size();
    }

    @Override
    public Object getItem(int position)
    {
        return list.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = convertView;
        if (view == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.rate_template, null);
        }

        Rate rate = (Rate) getItem(position);

        TextView amount = view.findViewById(R.id.rate_amount);
        amount.setText(rate.currency_symbol + df.format(rate.rate));

        TextView country_name = view.findViewById(R.id.rate_country_name);
        country_name.setText(rate.country_name);

        ImageView flag_img = view.findViewById(R.id.rate_flag);
        Drawable d = getFlagResource(rate.currency_code,context);
        if (d!=null) {
            flag_img.setImageDrawable(getFlagResource(rate.currency_code,context));
        }

        return view;
    }
}
