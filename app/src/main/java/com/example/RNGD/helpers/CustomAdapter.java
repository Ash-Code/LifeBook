package com.example.RNGD.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.RNGD.lifebook.R;

import java.util.ArrayList;

/**
 * Created by Ashwin Kumar on 8/6/2014.
 */
public class CustomAdapter extends ArrayAdapter<Data> {
    Context context;
    ArrayList<Data> list;


    public CustomAdapter(Context context, int resource, ArrayList<Data> list) {
        super(context, resource, list);
        this.list = list;
        this.context = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {//convertView is not null when the listview is recycling, otherwise it is

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {//this modification changes tonnes of resources. No need to inflate everytime.
            //further enhancement by the ConvertView or ViewHolder Pattern
            convertView = inflater.inflate(R.layout.data_layout, parent, false);

        }
        Data data = list.get(position);

        TextView title = (TextView) convertView.findViewById(R.id.title);
        title.setText(data.title);
        TextView date = (TextView) convertView.findViewById(R.id.time);
        date.setText(data.date);
        TextView body = (TextView) convertView.findViewById(R.id.text);
        body.setText(data.body);
        ImageView image = (ImageView) convertView.findViewById(R.id.coverImg);
        if (data.coverImg != null)
            image.setImageBitmap(data.coverImg);
        else
            image.setImageResource(R.drawable.ic_launcher);
        return convertView;
    }


}
