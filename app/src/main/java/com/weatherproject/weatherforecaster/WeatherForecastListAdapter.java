package com.weatherproject.weatherforecaster;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

public class WeatherForecastListAdapter extends ArrayAdapter<TempByDate>
{
    private final Context context;

    private final ArrayList<TempByDate> values;

    public WeatherForecastListAdapter(Context context, int textViewResourceId, ArrayList<TempByDate> values)
    {
        super(context, textViewResourceId, values);
        this.context = context;
        this.values = values;
    }

    static class ViewHolder
    {
        TextView date;
        TextView temperature;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        TempByDate tempByDate = values.get(position);

        if (convertView == null)
        {
            viewHolder = new ViewHolder();

            convertView = inflater.inflate(R.layout.listview_item_temperature, parent, false);
            viewHolder.date = (TextView) convertView.findViewById(R.id.tvDate);
            viewHolder.temperature = (TextView) convertView.findViewById(R.id.tvTemp);

            convertView.setTag(viewHolder);
        } else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        DateTimeFormatter dateTimeFormatter = DateTimeFormat.shortDate();
        String dateFormat = dateTimeFormatter.print(tempByDate.getDate().getTime());
        viewHolder.date.setText(dateFormat);
        viewHolder.temperature.setText(tempByDate.getTemperature());

        return convertView;
    }
}
