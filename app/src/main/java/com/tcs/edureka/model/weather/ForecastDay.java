package com.tcs.edureka.model.weather;

import java.util.Objects;

public class ForecastDay {
    private String date;
    private Day day;

    public ForecastDay(String date, Day day) {
        this.date = date;
        this.day = day;
    }

    public ForecastDay() {
    }

    @Override
    public String toString() {
        return "ForecastDay{" +
                "date='" + date + '\'' +
                ", day=" + day +
                '}';
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Day getDay() {
        return day;
    }

    public void setDay(Day day) {
        this.day = day;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ForecastDay that = (ForecastDay) o;

        if (!Objects.equals(date, that.date)) return false;
        return Objects.equals(day, that.day);
    }

    @Override
    public int hashCode() {
        int result = date != null ? date.hashCode() : 0;
        result = 31 * result + (day != null ? day.hashCode() : 0);
        return result;
    }
}
