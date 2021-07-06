package com.tcs.edureka.model.weather;

import com.google.gson.annotations.SerializedName;

public class Current {
    @SerializedName("temp_c")
    private double tempInC;

    @SerializedName("condition")
    private Condition condition;

    @SerializedName("feelslike_c")
    private double feelsLikeC;

    @SerializedName("wind_kph")
    private double windSpeed;
    @SerializedName("humidity")
    private double humidity;
    @SerializedName("uv")
    private double uv;


    public Current() {
    }

    public Current(double tempInC, Condition condition, double feelsLikeC, double windSpeed, double humidity, double uv) {
        this.tempInC = tempInC;
        this.condition = condition;
        this.feelsLikeC = feelsLikeC;
        this.windSpeed = windSpeed;
        this.humidity = humidity;
        this.uv = uv;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getUv() {
        return uv;
    }

    public void setUv(double uv) {
        this.uv = uv;
    }

    public double getTempInC() {
        return tempInC;
    }

    public void setTempInC(double tempInC) {
        this.tempInC = tempInC;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public double getFeelsLikeC() {
        return feelsLikeC;
    }

    public void setFeelsLikeC(double feelsLikeC) {
        this.feelsLikeC = feelsLikeC;
    }
}
