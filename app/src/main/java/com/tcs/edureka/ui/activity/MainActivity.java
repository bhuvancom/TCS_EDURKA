package com.tcs.edureka.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.tcs.edureka.R;
import com.tcs.edureka.databinding.ActivityMainBinding;
import com.tcs.edureka.receivers.AlarmReceiver;
import com.tcs.edureka.ui.activity.appointments.AppointmentActivity;
import com.tcs.edureka.ui.activity.contacts.ContactsActivity;
import com.tcs.edureka.ui.activity.map.MapActivity;
import com.tcs.edureka.ui.activity.weather.WeatherActivity;
import com.tcs.edureka.utility.Constants;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Bhuvaneshvar
 */


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View inflate = getLayoutInflater().inflate(R.layout.activity_main, null);
        ActivityMainBinding binding = ActivityMainBinding.bind(inflate);
        setContentView(binding.getRoot());

        requestPermissions(new String[]{
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.MODIFY_PHONE_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, 0);


        if (!(isPermissionGrantedForThis(this, Manifest.permission.READ_PHONE_STATE)
                || isPermissionGrantedForThis(this, Manifest.permission.MODIFY_PHONE_STATE)
                || isPermissionGrantedForThis(this, Manifest.permission.ACCESS_FINE_LOCATION)
                || isPermissionGrantedForThis(this, Manifest.permission.RECORD_AUDIO)
        )) {
            finish();
        }


        binding.btnOpenFragment.setOnClickListener((v) ->
                openMapActivity("Bhinga", "Bahraich")
        );


        binding.btnOpenFragment2.setOnClickListener(v -> {
            openContacts();
        });

        Calendar instance = Calendar.getInstance();
        instance.set(2021, 6, 04, 13, 25);
        Date date = instance.getTime();

        binding.btnOpen3.setOnClickListener(a -> {
            openAppointments("Tets title one two three",
                    date);
            //testBroadcast();
        });

        binding.btnOpen4.setOnClickListener(a -> {
            startActivity(new Intent(MainActivity.this, WeatherActivity.class));
        });
    }

    void openAppointments(String title, Date date) {
        Intent intent = new Intent(MainActivity.this, AppointmentActivity.class);
        intent.putExtra(Constants.EXTRA_DATE_AND_TIME, date);
        intent.putExtra(Constants.EXTRA_DATA_TITLE, title);
        startActivity(intent);
    }

    void testBroadcast() {
        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
        intent.setAction(Constants.APPOINTMENT_ACTION);
        intent.putExtra(Constants.EXTRA_DATA_TITLE, "TEST");
        intent.putExtra(Constants.EXTRA_DATE_AND_TIME, new Date());
        sendBroadcast(intent);
    }

    void openContacts() {
        Intent intent = new Intent(MainActivity.this, ContactsActivity.class);
        startActivity(intent);
    }

    boolean isPermissionGrantedForThis(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED;
    }

    void openMapActivity(String from, String to) {
        Log.d(TAG, "openMap: from " + from + " to " + to);
        Intent intent = new Intent(MainActivity.this, MapActivity.class);
        intent.putExtra(Constants.EXTRA_DATA_FROM, from);
        intent.putExtra(Constants.EXTRA_DATA_TO, to);
        startActivity(intent);
    }

    private void openUsbView() {
        Intent intent = new Intent(MainActivity.this, MapActivity.class);
        intent.putExtra(Constants.EXTRA_DATA_OPEN_USB, "USB");
        startActivity(intent);
    }
}