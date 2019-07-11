package com.willsdev.moneytransferapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
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
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listview_template, null);
        }

        final Wallet curr_wallet = list.get(position);

        TextView amount = view.findViewById(R.id.wallet_amount);
        amount.setText(curr_wallet.symbol + df.format(curr_wallet.balance));

        TextView country_name = view.findViewById(R.id.wallet_country_name);
        country_name.setText(curr_wallet.country);

        ImageView flag_img = view.findViewById(R.id.wallet_country_flag);
        flag_img.setImageDrawable(getFlagResource(curr_wallet.currency_code, context));

        final PopupWindow[] mPopupWindow = new PopupWindow[1];

        LinearLayout container = view.findViewById(R.id.wallet_container);
        container.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.popup_window, null);
                popupView.setClipToOutline(true);

                mPopupWindow[0] = new PopupWindow(
                        popupView,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                mPopupWindow[0].setElevation(5.0f);

                // Set up textboxes and spinners
                final EditText from_amt = popupView.findViewById(R.id.popup_transfer_from_amount);
                final EditText to_amt = popupView.findViewById(R.id.popup_transfer_to_amount);
                Spinner from_spinner = popupView.findViewById(R.id.popup_transfer_from_spinner);
                final Spinner to_spinner = popupView.findViewById(R.id.popup_transfer_to_spinner);
                final TextView rate = popupView.findViewById(R.id.popup_transfer_rate);

                to_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                    {
                        float rate_amt = ((MyApplication)context).rates.get(to_spinner.getSelectedItem().toString()).rate;
                        double convert;
                        double commision = 0.97;
                        try {
                            convert = Double.parseDouble(from_amt.getText().toString());
                        } catch (Exception e) {
                            //NAN
                            convert = 0;
                        }
                        to_amt.setText(convert*rate_amt*commision+"");
                        rate.setText(rate_amt+"");
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent)
                    {

                    }
                });

                Map<String,Object> data = new HashMap<>();
                data.put("from_spinner",from_spinner);
                data.put("to_spinner",to_spinner);
                data.put("popup",true);
                RunQuery r = new RunQuery(null,QueryType.GET_WALLETS,data);
                NetworkThread networkThread = new NetworkThread(r,(MainActivity)context);
                networkThread.execute();

                // Set up buttons
                Button cancel_btn = popupView.findViewById(R.id.popup_transfer_cancel);
                Button confirm_btn = popupView.findViewById(R.id.popup_transfer_confirm);
                cancel_btn.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        mPopupWindow[0].dismiss();
                    }
                });
                confirm_btn.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {

                    }
                });
            }
        });

        return view;
    }
}