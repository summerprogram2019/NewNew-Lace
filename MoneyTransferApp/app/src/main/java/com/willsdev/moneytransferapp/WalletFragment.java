package com.willsdev.moneytransferapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;


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
        ListView listView = view.findViewById(R.id.rate_scroll);

        String rateJSON = getArguments().getString("rates");
        List<Rate> rate_list = deserialise(rateJSON);

        RateListAdapter adapter = new RateListAdapter(rate_list,getActivity().getApplicationContext());
        listView.setAdapter(adapter);
        return view;
    }

    public static List<Rate> deserialise (String s) {
        try {
            byte[] b = Base64.decode(s.getBytes(), Base64.DEFAULT);
            ByteArrayInputStream bi = new ByteArrayInputStream(b);
            ObjectInputStream si = new ObjectInputStream(bi);
            return (List<Rate>) si.readObject();
        } catch (Exception e) {
        }
        return new ArrayList<>();
    }
}
