package com.tcs.edureka.model.weather;

import com.google.gson.annotations.SerializedName;

public class Location {
    private String name;
    private String region;
    private String country;
    private String localtime;
    @SerializedName("localtime_epoch")
    private Long date;

    public Location(String name, String region, String country, String localtime) {
        this.name = name;
        this.region = region;
        this.country = country;
        this.localtime = localtime;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLocaltime() {
        return localtime;
    }

    public void setLocaltime(String localtime) {
        this.localtime = localtime;
    }

    @Override
    public String toString() {
        return "Location{" +
                "name='" + name + '\'' +
                ", region='" + region + '\'' +
                ", country='" + country + '\'' +
                ", localtime='" + localtime + '\'' +
                '}';
    }
}
