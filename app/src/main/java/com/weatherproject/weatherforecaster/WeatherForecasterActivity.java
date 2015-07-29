package com.weatherproject.weatherforecaster;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;


public class WeatherForecasterActivity extends AppCompatActivity implements AsyncForecastResponse, AsyncGeocoderResponse, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
    private GoogleApiClient client;
    private Location loc;

    private EditText editText;
    private String geocoderAPICall;

    private boolean isConnected = false;

    private WeatherForecastAsyncTask forecastTask;

    private boolean isChecked = false;

    @Override
    public void onConnected(Bundle bundle)
    {
        isConnected = true;

        loc = LocationServices.FusedLocationApi.getLastLocation(client);


        CheckBox cbxGPS = (CheckBox) this.findViewById(R.id.cbxGPS);

        cbxGPS.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (((CheckBox) v).isChecked())
                {
                    isChecked = true;
                    editText.setEnabled(false);
                }
                else
                {
                    isChecked = false;
                    editText.setEnabled(true);
                }
            }
        });



    }

    @Override
    public void onConnectionSuspended(int i)
    {
        isConnected = false;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {
        isConnected = false;
    }

    public void onButtonClick(View view)
    {
        if(isConnected)
        {
            //If checkbox is checked fetch coordinates from location services
            //If not checked reverse geocode zipcode to retrieve coordinates
            if (isChecked)
            {
                String apiKey = "83caaf39db2d7638f103aef853a8b4af";
                String WebAPI = "http://api.openweathermap.org/data/2.5/forecast/daily?appid=" + apiKey + "&lat=" + loc.getLatitude() + "&lon=" + loc.getLongitude() + "&cnt=7&mode=json";

                try
                {
                    forecastTask = new WeatherForecastAsyncTask(this);

                    forecastTask.delegate = this;
                    forecastTask.execute(new URL(WebAPI));

                }
                catch (MalformedURLException e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                String inputText = editText.getText().toString();

                geocoderAPICall = "https://maps.googleapis.com/maps/api/geocode/json?address=" + inputText + "&key=AIzaSyATmUUmyPA6XLf4RxX1R9Rw8CdTtQW36H0";

                try
                {
                    GeocoderAsyncTask geocoderAsyncTask = new GeocoderAsyncTask(this);

                    geocoderAsyncTask.delegate = this;
                    geocoderAsyncTask.execute(new URL(geocoderAPICall));

                }
                catch (MalformedURLException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_weather_forecaster);

        client = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        editText = (EditText)this.findViewById(R.id.tbxZipCode);

    }

    @Override
    protected void onStart()
    {
        super.onStart();
        client.connect();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        client.disconnect();
    }

    public void processGeocoderResponse (String result)
    {
        try
        {
            LatLng coordResult = null;

            JSONObject object = new JSONObject(result);
            JSONArray resultsArray = object.getJSONArray("results");
            if (resultsArray.length() == 0)
            {
                Toast.makeText(this, "Search turned no results", Toast.LENGTH_LONG).show();
            }
            else
            {
                double lat = resultsArray.getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                double lng = resultsArray.getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");

                coordResult = new LatLng(lat, lng);

            }

            if (coordResult != null)
            {
                String apiKey = "83caaf39db2d7638f103aef853a8b4af";
                String weatherAPI = "http://api.openweathermap.org/data/2.5/forecast/daily?appid=" + apiKey + "&lat=" + coordResult.latitude + "&lon=" + coordResult.longitude + "&cnt=7&mode=json";

                try
                {
                    forecastTask = new WeatherForecastAsyncTask(this);

                    forecastTask.delegate = this;
                    forecastTask.execute(new URL(weatherAPI));

                }
                catch (MalformedURLException e)
                {
                    e.printStackTrace();
                }
            }
        }
        catch(JSONException ex)
        {
            ex.printStackTrace();
        }

    }

    @Override
    public void processForecastResponse(String result)
    {

        try
        {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("list");
            ArrayList<TempByDate> temps = new ArrayList<>();
            for(int i = 0; i < jsonArray.length(); i++)
            {
                TempByDate tbd = new TempByDate();
                double kelvinTemp = jsonArray.getJSONObject(i).getJSONObject("temp").getDouble("day");
                double fahrenheit = Math.round((kelvinTemp - 273.15) * 1.8 + 32);
                tbd.setTemperature(String.valueOf(fahrenheit));

                Long longDate = jsonArray.getJSONObject(i).getLong("dt");
                DateTime time = new DateTime(longDate * 1000);
                Date date = time.toDate();
                tbd.setDate(date);
                temps.add(tbd);

            }

            Intent intent = new Intent(this, WeatherTemperatures.class);
            intent.putExtra("Temperatures", temps);
            startActivity(intent);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

}
