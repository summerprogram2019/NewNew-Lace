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

import java.text.DecimalFormat;
import java.util.List;

import static com.willsdev.moneytransferapp.MainActivity.getFlagResource;

class Culture{
    String name;
    String ccode;
    String taboos;
    String finance;
    String tips;
}

public class CultureListAdapter extends BaseAdapter implements ListAdapter
{
    List<Culture> list;
    Context context;

    public CultureListAdapter(List<Culture> list, Context context) {
        this.list=list;
        this.context=context;
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
            //view = inflater.inflate(R.layout.culture_template, null);
        }

        Culture culture = (Culture) getItem(position);

        return view;
    }
}