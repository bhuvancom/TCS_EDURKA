package com.tcs.edureka.ui.fragments.weather

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.squareup.picasso.Picasso
import com.tcs.edureka.R
import com.tcs.edureka.databinding.FragmentWeeklyWeatherBinding
import com.tcs.edureka.ui.fragments.weather.viewmodel.WeatherState
import com.tcs.edureka.ui.fragments.weather.viewmodel.WeatherViewModel
import com.tcs.edureka.utility.Utility
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author Bhuvaneshvar
 */
@AndroidEntryPoint
class WeeklyWeatherFrag : Fragment(R.layout.fragment_weekly_weather) {
    private val TAG = "WeeklyWeatherFrag"
    lateinit var binding: FragmentWeeklyWeatherBinding
    lateinit var weatherViewModel: WeatherViewModel
    private val weatherAdapter by lazy {
        WeatherAdapter()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ")
        binding = FragmentWeeklyWeatherBinding.bind(view)

        weatherViewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)

        weatherViewModel.getNNumberDayWeather(Utility.getPreffCity(), 10)
        binding.recyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = weatherAdapter
        }

        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm aa")
        weatherViewModel.weeklyResult.observe(viewLifecycleOwner) {

            binding.progress.isVisible = it is WeatherState.LOADING
            binding.relative.isVisible = it !is WeatherState.LOADING
            binding.recyclerview.isVisible = it !is WeatherState.LOADING

            when (it) {
                is WeatherState.ERROR -> {
                    AlertDialog.Builder(requireContext())
                            .setTitle("Error Occurred")
                            .setMessage("An Error occurred due to ${it.exception.message}")
                            .setNeutralButton("Retry") { d, v ->
                                weatherViewModel.getNNumberDayWeather(Utility.getPreffCity())
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
                        city.text = it.data.location.name
                        date.text = "${sdf.format(Date())}"
                        condition.text = it.data.current.condition.condition
                        Picasso.get().load("https:" + it.data.current.condition.imgUrl)
                                .into(weatherResource)
                        tempCondition.text = "${it.data.current.tempInC} °C"

                        weatherAdapter.differ.submitList(it.data.forecast.forecastDays)
                    }
                }
            }
        }

    }
}

private const val TAG = "WeeklyWeatherFrag"