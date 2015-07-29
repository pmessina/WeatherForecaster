package com.weatherproject.weatherforecaster;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class WeatherForecastAsyncTask extends AsyncTask<URL, Integer, String>
{
    AsyncForecastResponse delegate = null;

    private final Context context;
    private Dialog dialog;

    public WeatherForecastAsyncTask(Context context)
    {
        this.context = context;
    }

    @Override
    protected void onPreExecute()
    {
        dialog = ProgressDialog.show(context, "One moment", "Fetching Weather");
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
    }


    @Override
    protected String doInBackground(URL... urls)
    {
        HttpHeaders requestHeaders = new HttpHeaders();
        List<MediaType> acceptableMediaTypes = new ArrayList<>();

        acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
        //acceptableMediaTypes.add(MediaType.APPLICATION_XML);
        requestHeaders.setAccept(acceptableMediaTypes);

        // Populate the headers in an HttpEntity object to use for the request
        HttpEntity<Object> requestEntity = new HttpEntity<>(requestHeaders);


        RestTemplate rt = new RestTemplate();
        rt.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        ResponseEntity<String> responseEntity = rt.exchange(urls[0].toString(), HttpMethod.GET, requestEntity, String.class);

        return responseEntity.getBody();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {

        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String result)
    {
        super.onPostExecute(result);
        dialog.dismiss();

        delegate.processForecastResponse(result);
    }
}
