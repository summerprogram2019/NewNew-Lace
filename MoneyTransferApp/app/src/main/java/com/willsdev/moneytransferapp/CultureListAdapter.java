package com.willsdev.moneytransferapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    Culture (String name, String ccode, String taboos, String finance) {
        this.name=name;
        this.ccode=ccode;
        this.taboos=taboos;
        this.finance=finance;
    }
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
            view = inflater.inflate(R.layout.culture_template, null);
        }

        Culture culture = (Culture) getItem(position);

        ImageView flag_img = view.findViewById(R.id.culture_country_flag);
        Drawable d = getFlagResource(culture.ccode,context);
        if (d!=null) {
            flag_img.setImageDrawable(getFlagResource(culture.ccode,context));
        }

        TextView country_name = view.findViewById(R.id.culture_country_name);
        country_name.setText(culture.name);

        TextView pmts = view.findViewById(R.id.country_pmts);
        pmts.setText(culture.finance);

        TextView taboos = view.findViewById(R.id.country_taboos);
        taboos.setText(culture.taboos);

        final LinearLayout shown = view.findViewById(R.id.culture_shown);
        final LinearLayout hidden = view.findViewById(R.id.culture_hidden);

        // Hide/show extra info
        shown.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(hidden.getVisibility()==View.GONE) {
                    hidden.setVisibility(View.VISIBLE);
                } else {
                    hidden.setVisibility(View.GONE);
                }
            }
        });

        return view;
    }
}