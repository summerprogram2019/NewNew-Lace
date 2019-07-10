package com.willsdev.moneytransferapp;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class RatesFragment extends Fragment
{
    List<Rate> list;

    public RatesFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rates, container, false);
        ListView listView = view.findViewById(R.id.rate_scroll);

        Map<String,Object> data = new HashMap<>();
        data.put("rate_listview",listView);
        NetworkThread networkThread = new NetworkThread(new RunQuery(null,QueryType.RATES,data),getActivity());
        networkThread.execute();

        return view;
    }
}
