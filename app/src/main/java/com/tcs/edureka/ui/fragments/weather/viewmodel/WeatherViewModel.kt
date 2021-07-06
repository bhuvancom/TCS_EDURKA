package com.tcs.edureka.ui.fragments.weather.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tcs.edureka.api.WeatherAPI
import com.tcs.edureka.model.weather.WeatherModel
import com.tcs.edureka.utility.RetroCreator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject


/**
 * @author Bhuvaneshvar
 */
@HiltViewModel
class WeatherViewModel @Inject constructor(private val api: WeatherAPI) : ViewModel() {

    private val _result = MutableLiveData<WeatherState<WeatherModel>>(WeatherState.LOADING)
    val result: LiveData<WeatherState<WeatherModel>> = _result

    private val _weekResult = MutableLiveData<WeatherState<WeatherModel>>(WeatherState.LOADING)
    val weeklyResult: LiveData<WeatherState<WeatherModel>> = _weekResult

    fun getCurrentDayWeather(city: String) {
        val map = HashMap<String, String>()
        map["q"] = city
        map["key"] = RetroCreator.KEY
        viewModelScope.launch {
            getWeather(map, 2)
        }
    }

    fun getNNumberDayWeather(city: String, howManyDay: Int = 10) {
        val map = HashMap<String, String>()
        map["q"] = city
        map["key"] = RetroCreator.KEY
        map["days"] = howManyDay.toString()

        viewModelScope.launch {
            getWeather(map, 1)
        }
    }

    private suspend fun getWeather(map: HashMap<String, String>, type: Int) {
        Log.d(TAG, "getWeather: ${map["q"]}")
        viewModelScope.launch {
            try {
                if (type == 1) _weekResult.value = WeatherState.LOADING
                else _result.value = WeatherState.LOADING

                val weather: Response<WeatherModel> = if (type == 1) {
                    api.getWeather(map)
                } else {
                    api.getCurrentWeather(map)
                }

                if (weather.isSuccessful && weather.body() != null) {
                    if (type == 2) _result.value = WeatherState.SUCCESS(weather.body()!!)
                    else _weekResult.value = WeatherState.SUCCESS(weather.body()!!)
                } else {
                    if (type == 2) _result.value = WeatherState.ERROR(RuntimeException("No data received"))
                    else _weekResult.value = WeatherState.ERROR(RuntimeException("No data received"))
                }
            } catch (error: Exception) {
                if (type == 2) _result.value = WeatherState.ERROR(error)
                else _weekResult.value = WeatherState.ERROR(error)
            }
        }
    }


}

private const val TAG = "WeatherViewModel"