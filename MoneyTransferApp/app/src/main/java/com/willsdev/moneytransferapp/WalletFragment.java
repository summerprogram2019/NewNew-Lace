package com.willsdev.moneytransferapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class WalletFragment extends Fragment
{


    public WalletFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_wallet, container, false);
        ListView listView = view.findViewById(R.id.wallet_scroll);

        Map<String,Object> data = new HashMap<>();
        data.put("wallet_listview",listView);
        data.put("popup",false);
        NetworkThread networkThread = new NetworkThread(new RunQuery(null,QueryType.GET_WALLETS,data),getActivity());
        networkThread.execute();

        final PopupWindow[] mPopupWindow = new PopupWindow[1];

        Button add_wallet_btn = view.findViewById(R.id.add_wallet_btn);
        add_wallet_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                final View popupView = inflater.inflate(R.layout.add_wallet_popup, null);


                mPopupWindow[0] = new PopupWindow(
                        popupView,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                mPopupWindow[0].setElevation(100.0f);

                final Spinner country_spinner = popupView.findViewById(R.id.popup_add_wallet_country);
                final Button confirm_btn = popupView.findViewById(R.id.popup_wallet_confirm);
                final Button cancel_btn = popupView.findViewById(R.id.popup_wallet_cancel);

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
                        String ccode = country_spinner.getSelectedItem().toString();
                        Map<String,Object> data = new HashMap<>();
                        data.put("ccode",ccode);
                        RunQuery runQuery = new RunQuery(null,QueryType.ADD_WALLET,data);
                        NetworkThread networkThread1 = new NetworkThread(runQuery,getActivity());
                        networkThread1.execute();
                        mPopupWindow[0].dismiss();
                        Intent intent = new Intent(getActivity().getApplication().getApplicationContext(), MainActivity.class);
                        getActivity().getApplicationContext().startActivity(intent);
                    }
                });

                Map<String,Object> data = new HashMap<>();
                data.put("spinner",country_spinner);
                RunQuery r = new RunQuery(null,QueryType.WALLETS_LEFT,data);
                NetworkThread networkThread = new NetworkThread(r,getActivity());
                networkThread.execute();

                mPopupWindow[0].setFocusable(true);
                //mPopupWindow[0].update();
                mPopupWindow[0].showAtLocation(view, Gravity.CENTER, 0, 0);
            }
        });

        
//        String rateJSON = getArguments().getString("rates");
//        List<Rate> rate_list = deserialise(rateJSON);
//
//        RateListAdapter adapter = new RateListAdapter(rate_list,getActivity().getApplicationContext());
//        listView.setAdapter(adapter);
        return view;
    }
}
