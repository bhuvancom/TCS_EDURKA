package com.tcs.edureka.model.weather;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Forecast {

    @SerializedName("forecastday")
    List<ForecastDay> forecastDays;

    public Forecast() {
    }

    public Forecast(List<ForecastDay> forecastDays) {
        this.forecastDays = forecastDays;
    }

    public List<ForecastDay> getForecastDays() {
        return forecastDays;
    }

    public void setForecastDays(List<ForecastDay> forecastDays) {
        this.forecastDays = forecastDays;
    }

    @Override
    public String toString() {
        return "Forecast{" +
                "forecastDays=" + forecastDays +
                '}';
    }
}
