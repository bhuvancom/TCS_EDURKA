package com.tcs.edureka.ui.fragments.weather

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.squareup.picasso.Picasso
import com.tcs.edureka.R
import com.tcs.edureka.databinding.FragmentDailyWeatherBinding
import com.tcs.edureka.ui.fragments.weather.viewmodel.WeatherState
import com.tcs.edureka.ui.fragments.weather.viewmodel.WeatherViewModel
import com.tcs.edureka.utility.Utility
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class DailyWeatherFrag : Fragment(R.layout.fragment_daily_weather) {
    lateinit var binding: FragmentDailyWeatherBinding
    private val TAG = "DailyWeatherFrag"
    lateinit var weatherViewModel: WeatherViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDailyWeatherBinding.bind(view)

        weatherViewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)
        weatherViewModel.getCurrentDayWeather(Utility.PREFF_CITY)

        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm aa")

        weatherViewModel.result.observe(viewLifecycleOwner) {
            binding.relative.isVisible = it !is WeatherState.LOADING
            binding.ll.isVisible = it !is WeatherState.LOADING
            binding.progressCircular.isVisible = it is WeatherState.LOADING

            when (it) {
                is WeatherState.ERROR -> {
                    AlertDialog.Builder(requireContext())
                            .setTitle("Error Occurred")
                            .setMessage("An Error occurred due to ${it.exception.message}")
                            .setNeutralButton("Retry") { d, v ->
                                weatherViewModel.getCurrentDayWeather(Utility.getPreffCity())
                                d.dismiss()
                            }
                            .setPositiveButton("Cancel") { d, v ->
                                d.dismiss()
                            }.show()
                    Toast.makeText(requireContext(), "Error ${it.exception.message}", Toast.LENGTH_LONG).show()
                    Log.d(TAG, "onViewCreated: error ${it.exception}")
                }

                is WeatherState.LOADING -> {
                    Log.d(TAG, "onViewCreated: it is laoding")
                }

                is WeatherState.SUCCESS -> {
                    binding.apply {
//                        val dateV = it.data.location.date
//                        val atZone = Instant.ofEpochMilli(dateV).atZone(ZoneId.of("IST"))
//                        val format = sdf.format(atZone)
                        city.text = it.data.location.name
                        date.text = sdf.format(Date())
                        condition.text = it.data.current.condition.condition
                        Picasso.get().load("https:" + it.data.current.condition.imgUrl)
                                .into(weatherResource)
                        tempCondition.text = "${it.data.current.tempInC} °C"
                        temperature.text = "${it.data.current.feelsLikeC} °C"
                        humidityValue.text = "${it.data.current.humidity}"
                        windValue.text = "${it.data.current.windSpeed} km/h"
                        uvValue.text = "${it.data.current.uv}"
                    }
                }
            }
        }

    }
}