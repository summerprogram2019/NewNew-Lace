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
public class CultureFragment extends Fragment
{


    public CultureFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_culture, container, false);
        ListView listView = view.findViewById(R.id.culture_scroll);

        Map<String,Object> data = new HashMap<>();
        data.put("cultures_listview",listView);
        NetworkThread networkThread = new NetworkThread(new RunQuery(null,QueryType.CULTURES,data),getActivity());
        networkThread.execute();
        return view;
    }

}
