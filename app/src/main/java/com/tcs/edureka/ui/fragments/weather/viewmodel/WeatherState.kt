package com.tcs.edureka.ui.fragments.weather.viewmodel

/**
 * @author Bhuvaneshvar
 */
sealed class WeatherState<out T> {
    data class SUCCESS<out T>(val data: T) : WeatherState<T>()
    object LOADING : WeatherState<Nothing>()
    data class ERROR(val exception: Exception) : WeatherState<Nothing>()
}