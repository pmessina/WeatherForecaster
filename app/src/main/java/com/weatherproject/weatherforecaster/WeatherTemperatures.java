package com.weatherproject.weatherforecaster;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;


public class WeatherTemperatures extends ListActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_temperatures);

        ListView listView = this.getListView();

        ArrayList<TempByDate> temps = (ArrayList<TempByDate>)getIntent().getExtras().getSerializable("Temperatures");

        WeatherForecastListAdapter arrayAdapter = new WeatherForecastListAdapter(this, android.R.layout.simple_list_item_1, temps);

        listView.setAdapter(arrayAdapter);


    }



}
