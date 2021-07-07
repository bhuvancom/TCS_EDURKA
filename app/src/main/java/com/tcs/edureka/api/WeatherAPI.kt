package com.tcs.edureka.api

import com.tcs.edureka.model.weather.WeatherModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap
import java.util.*


/**
 * @author Bhuvaneshvar
 */
interface WeatherAPI {
    @GET("forecast.json")
    suspend fun getWeather(@QueryMap map: HashMap<String, String>): Response<WeatherModel>

    @GET("current.json")
    suspend fun getCurrentWeather(@QueryMap map: HashMap<String, String>): Response<WeatherModel>
}