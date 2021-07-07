package com.tcs.edureka.ui.activity.weather

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.tcs.edureka.R
import com.tcs.edureka.databinding.ActivityWeatheBinding
import com.tcs.edureka.ui.activity.MyPreferencesActivity
import com.tcs.edureka.utility.Constants
import com.tcs.edureka.utility.Utility
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author Bhuvaneshvar + UI By fathima
 */
@AndroidEntryPoint
class WeatherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Utility.PREFF_CITY = Utility.getFromSharedPref(Constants.PREFERRED_CITY, this)

        if (Utility.PREFF_CITY.isBlank()) {
            val intent = Intent(this, MyPreferencesActivity::class.java)
            Utility.makeToast("Select city first", this)
            startActivity(intent)
        }

        val binding = ActivityWeatheBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = supportFragmentManager.findFragmentById(R.id.navHostFrag)
        val control = navController?.findNavController()
        NavigationUI.setupWithNavController(binding.bottomNavBar, control!!)
    }
}