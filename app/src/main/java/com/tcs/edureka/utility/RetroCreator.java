package com.tcs.edureka.utility;

import com.tcs.edureka.api.WeatherAPI;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class RetroCreator {
    public static final String BASE_URL = "https://api.weatherapi.com/v1/";
    public static final String KEY = "9a46fa7b4e784fcda8e85150211206";
    private static RetroCreator instance;
    private static Retrofit retrofit;

    public static RetroCreator getInstance() {
        if (instance == null) {
            instance = new RetroCreator();
            getRetrofit();
        }
        return instance;
    }

    private static Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }

    public WeatherAPI getApiCallSerive() {
        return getRetrofit().create(WeatherAPI.class);
    }
}
