package com.example.RNGD.lifebook;

import android.content.Context;
import android.net.ParseException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

/**
 * Created by Ashwin Kumar on 8/6/2014.
 */
public class CustomAdapter extends ArrayAdapter<Data> {
    Context context;
    ArrayList<Data> list;
    public static String formatDateTime(Context context, String timeToFormat) {

        String finalDateTime = "";

        SimpleDateFormat iso8601Format = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");

        Date date = null;
        if (timeToFormat != null) {
            try {
                date = iso8601Format.parse(timeToFormat);
            } catch (ParseException e) {
                date = null;
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }

            if (date != null) {
                long when = date.getTime();
                int flags = 0;
                flags |= android.text.format.DateUtils.FORMAT_SHOW_TIME;
                flags |= android.text.format.DateUtils.FORMAT_SHOW_DATE;
                flags |= android.text.format.DateUtils.FORMAT_ABBREV_MONTH;
                flags |= android.text.format.DateUtils.FORMAT_SHOW_YEAR;

                finalDateTime = android.text.format.DateUtils.formatDateTime(context,
                        when + TimeZone.getDefault().getOffset(when), flags);
            }
        }
        return finalDateTime;
    }
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
        title.setText(list.get(position).title);
        TextView date=(TextView)convertView.findViewById(R.id.time);
        date.setText(formatDateTime(context,list.get(position).date));
        TextView body = (TextView) convertView.findViewById(R.id.text);
        body.setText(list.get(position).body);
        return convertView;
    }
}
