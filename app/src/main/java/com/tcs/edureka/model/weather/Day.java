package com.tcs.edureka.model.weather;

import com.google.gson.annotations.SerializedName;

public class Day {
    @SerializedName("maxtemp_c")
    private double maxTempC;
    @SerializedName("mintemp_c")
    private double minTempC;

    private Condition condition;

    public Day() {
    }

    public Day(double maxTempC, double minTempC, Condition condition) {
        this.maxTempC = maxTempC;
        this.minTempC = minTempC;
        this.condition = condition;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public double getMaxTempC() {
        return maxTempC;
    }

    public void setMaxTempC(double maxTempC) {
        this.maxTempC = maxTempC;
    }

    public double getMinTempC() {
        return minTempC;
    }

    public void setMinTempC(double minTempC) {
        this.minTempC = minTempC;
    }

    @Override
    public String toString() {
        return "Day{" +
                "maxTempC='" + maxTempC + '\'' +
                ", minTempC='" + minTempC + '\'' +
                ", condition=" + condition +
                '}';
    }
}
