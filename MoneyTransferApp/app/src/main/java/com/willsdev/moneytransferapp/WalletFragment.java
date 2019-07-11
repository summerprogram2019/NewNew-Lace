package com.willsdev.moneytransferapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);
        ListView listView = view.findViewById(R.id.wallet_scroll);

        Map<String,Object> data = new HashMap<>();
        data.put("wallet_listview",listView);
        data.put("popup",false);
        NetworkThread networkThread = new NetworkThread(new RunQuery(null,QueryType.GET_WALLETS,data),getActivity());
        networkThread.execute();

//        String rateJSON = getArguments().getString("rates");
//        List<Rate> rate_list = deserialise(rateJSON);
//
//        RateListAdapter adapter = new RateListAdapter(rate_list,getActivity().getApplicationContext());
//        listView.setAdapter(adapter);
        return view;
    }
}
