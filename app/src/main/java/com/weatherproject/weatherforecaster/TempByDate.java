package com.weatherproject.weatherforecaster;

import java.io.Serializable;
import java.util.Date;

public class TempByDate implements Serializable
{
    private Date date;
    private String temperature;

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    public String getTemperature()
    {
        return temperature;
    }

    public void setTemperature(String temperature)
    {
        this.temperature = temperature;
    }
}
