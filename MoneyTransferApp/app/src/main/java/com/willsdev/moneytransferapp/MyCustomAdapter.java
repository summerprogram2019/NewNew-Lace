package com.willsdev.moneytransferapp;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
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

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private Activity activity;
    private List<Wallet> list;
    DecimalFormat df = new DecimalFormat("0.00");

    public MyCustomAdapter(List<Wallet> list, Activity activity)
    {
        this.list = list;
        this.activity = activity;
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
            LayoutInflater inflater = (LayoutInflater) activity.getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listview_template, null);
        }

        final Wallet curr_wallet = list.get(position);

        TextView amount = view.findViewById(R.id.wallet_amount);
        amount.setText(curr_wallet.symbol + df.format(curr_wallet.balance));

        final TextView country_name = view.findViewById(R.id.wallet_country_name);
        country_name.setText(curr_wallet.country);

        ImageView flag_img = view.findViewById(R.id.wallet_country_flag);
        flag_img.setImageDrawable(getFlagResource(curr_wallet.currency_code, activity.getApplicationContext()));

        final PopupWindow[] mPopupWindow = new PopupWindow[1];


        final LinearLayout container = view.findViewById(R.id.wallet_container);

        container.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                LayoutInflater inflater = activity.getLayoutInflater();
                View popupView = inflater.inflate(R.layout.popup_window, null);


                mPopupWindow[0] = new PopupWindow(
                        popupView,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                mPopupWindow[0].setElevation(100.0f);
                mPopupWindow[0].setWidth(1000);

                // Set up textboxes and spinners
                final EditText from_amt = popupView.findViewById(R.id.popup_transfer_from_amount);
                final EditText to_amt = popupView.findViewById(R.id.popup_transfer_to_amount);
                final Spinner from_spinner = popupView.findViewById(R.id.popup_transfer_from_spinner);
                final Spinner to_spinner = popupView.findViewById(R.id.popup_transfer_to_spinner);
                final TextView rate = popupView.findViewById(R.id.popup_transfer_rate);

                final double commision = 0.97;

                from_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                    {
                        float from_rate_amt = ((MyApplication)activity.getApplicationContext()).rates.get(from_spinner.getSelectedItem().toString()).rate;
                        float to_rate_amt = ((MyApplication)activity.getApplicationContext()).rates.get(to_spinner.getSelectedItem().toString()).rate;
                        double convert;
                        try {
                            convert = Double.parseDouble(from_amt.getText().toString());
                        } catch (Exception e) {
                            //NAN
                            convert = 0;
                        }
                        to_amt.setText(convert*(to_rate_amt/from_rate_amt)*commision+"");
                        rate.setText(to_rate_amt/from_rate_amt+"");
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent)
                    {

                    }
                });

                to_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                    {
                        float from_rate_amt = ((MyApplication)activity.getApplicationContext()).rates.get(from_spinner.getSelectedItem().toString()).rate;
                        float to_rate_amt = ((MyApplication)activity.getApplicationContext()).rates.get(to_spinner.getSelectedItem().toString()).rate;
                        double convert;
                        try {
                            convert = Double.parseDouble(from_amt.getText().toString());
                        } catch (Exception e) {
                            //NAN
                            convert = 0;
                        }
                        to_amt.setText(convert*(to_rate_amt/from_rate_amt)*commision+"");
                        rate.setText(to_rate_amt/from_rate_amt+"");
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent)
                    {

                    }
                });

                from_amt.addTextChangedListener(new TextWatcher()
                {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after)
                    {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count)
                    {

                    }

                    @Override
                    public void afterTextChanged(Editable s)
                    {
                        float from_rate_amt = ((MyApplication)activity.getApplicationContext()).rates.get(from_spinner.getSelectedItem().toString()).rate;
                        float to_rate_amt = ((MyApplication)activity.getApplicationContext()).rates.get(to_spinner.getSelectedItem().toString()).rate;
                        double convert;
                        try {
                            convert = Double.parseDouble(from_amt.getText().toString());
                        } catch (Exception e) {
                            //NAN
                            convert = 0;
                        }
                        to_amt.setText(convert*(to_rate_amt/from_rate_amt)*commision+"");
                        rate.setText(to_rate_amt/from_rate_amt+"");
                    }
                });

                Map<String,Object> data = new HashMap<>();
                data.put("from_spinner",from_spinner);
                data.put("to_spinner",to_spinner);
                data.put("popup",true);
                RunQuery r = new RunQuery(null,QueryType.GET_WALLETS,data);
                NetworkThread networkThread = new NetworkThread(r,activity);
                networkThread.execute();

                // Set up buttons
                Button cancel_btn = popupView.findViewById(R.id.popup_wallet_cancel);
                Button confirm_btn = popupView.findViewById(R.id.popup_wallet_confirm);
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
                        float from_rate_amt = ((MyApplication)activity.getApplicationContext()).rates.get(from_spinner.getSelectedItem().toString()).rate;
                        float to_rate_amt = ((MyApplication)activity.getApplicationContext()).rates.get(to_spinner.getSelectedItem().toString()).rate;
                        double convert;
                        try {
                            convert = Double.parseDouble(from_amt.getText().toString());
                        } catch (Exception e) {
                            //NAN
                            convert = 0;
                        }

                        Map<String,Object> data = new HashMap<>();
                        data.put("country_from",from_spinner.getSelectedItem().toString());
                        data.put("country_to",to_spinner.getSelectedItem().toString());
                        data.put("from_amt",Double.parseDouble(from_amt.getText().toString()));
                        data.put("to_amt",Double.parseDouble(to_amt.getText().toString()));
                        data.put("rate",convert*(to_rate_amt/from_rate_amt)*commision);
                        RunQuery runQuery = new RunQuery(null,QueryType.TRANSFER,data);
                        NetworkThread networkThread1 = new NetworkThread(runQuery,activity);
                        networkThread1.execute();
                        mPopupWindow[0].dismiss();
                        Intent intent = new Intent(activity.getApplication().getApplicationContext(), MainActivity.class);
                        activity.getApplicationContext().startActivity(intent);
                    }
                });

                mPopupWindow[0].setFocusable(true);
                //mPopupWindow[0].update();
                mPopupWindow[0].showAtLocation(container, Gravity.CENTER, 0, 0);
            }
        });

        return view;
    }
}