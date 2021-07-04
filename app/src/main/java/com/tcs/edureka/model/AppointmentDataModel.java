package com.tcs.edureka.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class AppointmentDataModel {

    @PrimaryKey(autoGenerate = true)
    private Long id;
    private String title;
    private int month;
    private int date;
    private int year;
    private int hour;
    private int minute;

    public AppointmentDataModel() {
    }

    @Ignore
    public AppointmentDataModel(
            String title,
            int month,
            int date,
            int year,
            int hour,
            int minute
    ) {
        this.title = title;
        this.month = month;
        this.date = date;
        this.year = year;
        this.hour = hour;
        this.minute = minute;

    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }


    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
