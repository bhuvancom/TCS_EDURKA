package com.tcs.edureka.ui.activity.weather

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.tcs.edureka.R
import com.tcs.edureka.databinding.ActivityWeatheBinding
import com.tcs.edureka.ui.activity.MainActivity
import com.tcs.edureka.utility.Utility
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author Bhuvaneshvar
 */
@AndroidEntryPoint
class WeatherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        Utility.PREFF_CITY = "bahraich" //todo set here after getting from pref

        if (Utility.PREFF_CITY.isBlank()) {
            val intent = Intent(this, MainActivity::class.java) //todo: take user to shared pref
            Utility.makeToast("Select city first", this)
            startActivity(intent)
            finishAffinity()
        }

        val binding = ActivityWeatheBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = supportFragmentManager.findFragmentById(R.id.navHostFrag)
        val control = navController?.findNavController()
        NavigationUI.setupWithNavController(binding.bottomNavBar, control!!)
    }
}