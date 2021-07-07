package com.tcs.edureka.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.slice.widget.SliceLiveData;
import androidx.slice.widget.SliceView;

import com.squareup.picasso.Picasso;
import com.tcs.edureka.R;
import com.tcs.edureka.databinding.ActivityMainBinding;
import com.tcs.edureka.model.weather.WeatherModel;
import com.tcs.edureka.receivers.AlarmReceiver;
import com.tcs.edureka.receivers.SpeechRecognitionReceiver;
import com.tcs.edureka.services.SpeechRecognitionService;
import com.tcs.edureka.ui.activity.appointments.AppointmentActivity;
import com.tcs.edureka.ui.activity.contacts.ContactsActivity;
import com.tcs.edureka.ui.activity.map.MapActivity;
import com.tcs.edureka.ui.activity.media.MyMediaPlayerActivity;
import com.tcs.edureka.ui.activity.weather.WeatherActivity;
import com.tcs.edureka.ui.fragments.weather.viewmodel.WeatherState;
import com.tcs.edureka.ui.fragments.weather.viewmodel.WeatherViewModel;
import com.tcs.edureka.utility.Constants;
import com.tcs.edureka.utility.Utility;

import java.util.Date;

import dagger.hilt.android.AndroidEntryPoint;

import static android.view.View.GONE;

/**
 * @author Bhuvaneshvar
 */

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    Intent speechRecognitionServiceIntent;
    SpeechRecognitionReceiver speechRecognitionReceiver;
    boolean shouldMicWork;
    boolean shouldSliceWork;
    ActivityMainBinding binding;

    void testSpeech() {
        Intent intent = new Intent(MainActivity.this, SpeechRecognitionReceiver.class);
        intent.setAction(Constants.BROADCAST_ACTION_SPEECH_RECOGNITION);
        sendBroadcast(intent);
    }

    void testService() {
        Intent intent = new Intent(this, SpeechRecognitionService.class);
        startService(intent);
        speechRecognitionReceiver = new SpeechRecognitionReceiver();
        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(
                speechRecognitionReceiver, new IntentFilter(Constants.BROADCAST_ACTION_SPEECH_RECOGNITION));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterSpeechRecognition();
    }

    void unRegisterSpeechRecognition() {
        if (speechRecognitionServiceIntent != null) {
            stopService(speechRecognitionServiceIntent);
        }
        LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(speechRecognitionReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View inflate = getLayoutInflater().inflate(R.layout.activity_main, null);
        binding = ActivityMainBinding.bind(inflate);
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


        shouldMicWork = Utility.getCheckedFromPref(Constants.SPEECH, this);
        invalidateOptionsMenu();


        testService();

        Intent intent = new Intent();
        String packageName = getPackageName();
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + packageName));
            startActivity(intent);
        }

        String userName = Utility.getFromSharedPref(Constants.USERNAME, this);
        if (userName != null && !userName.isEmpty()) {
            binding.textdashboard.setText("Hello, " + userName);
        }

        //startActivity(new Intent(MainActivity.this, MyMediaPlayerActivity.class));

        WeatherViewModel weatherViewModel = new ViewModelProvider(this).get(WeatherViewModel.class);

        Utility.PREFF_CITY = Utility.getFromSharedPref(Constants.PREFERRED_CITY, this);

        if (!Utility.PREFF_CITY.trim().isEmpty()) {
            weatherViewModel.getCurrentDayWeather(Utility.getPreffCity());
            weatherViewModel.getResult().observe(this, data -> {
                if (data instanceof WeatherState.ERROR) {
                    Log.d(TAG, "onCreate: error " + ((WeatherState.ERROR) data).getException());
                } else if (data instanceof WeatherState.LOADING) {
                    Log.d(TAG, "onCreate: loading ");
                } else if (data instanceof WeatherState.SUCCESS) {
                    WeatherModel data1 = ((WeatherState.SUCCESS<WeatherModel>) data).getData();
                    String weather = Utility.getPreffCity() + "\n" + data1.getCurrent().getCondition().getCondition()
                            + "\n" + data1.getCurrent().getTempInC() + " °C";
                    binding.tvWeather.setText(weather);

                    Picasso.get().load("https:" + data1.getCurrent().getCondition().getImgUrl())
                            .into(binding.IVweatherApplication);
                }
            });
        }


        binding.map.setOnClickListener(e ->
                openMapActivity("", "TCS"));

        binding.user.setOnClickListener(e ->
                openPrefScreen());

        binding.calendar.setOnClickListener(e ->

        {
            startActivity(new Intent(MainActivity.this, AppointmentActivity.class));
        });

        binding.contact.setOnClickListener(e ->

        {
            startActivity(new Intent(MainActivity.this, ContactsActivity.class));
        });

        binding.IVweatherApplication.setOnClickListener(e ->

        {
            startActivity(new Intent(MainActivity.this, WeatherActivity.class));
        });

        binding.mediaPlayer.setOnClickListener(e ->

        {
            startActivity(new Intent(MainActivity.this, MyMediaPlayerActivity.class));
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        shouldSliceWork = Utility.getCheckedFromPref(Constants.SLICE, this);
        invalidateOptionsMenu();

        if (shouldSliceWork) {
            SliceView sliceIcon = binding.sliceIcon;
            SliceLiveData.fromUri(this, Utility.getUri(this, "map"))
                    .observe(this, sliceIcon);
        } else {
            binding.sliceIcon.setVisibility(GONE);
        }

    }

    void openMusicWithActio(String action) {
        Intent intent1 = new Intent(MainActivity.this, MyMediaPlayerActivity.class);
        intent1.putExtra(Constants.MUSIC_ACTION, action);
        startActivity(intent1);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!shouldMicWork) {
            menu.removeItem(R.id.menu_mic);
        }
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    void openPrefScreen() {
        startActivity(new Intent(this, MyPreferencesActivity.class));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_open_pref) {
            openPrefScreen();
            return true;
        }

        if (item.getItemId() == R.id.menu_mic) {
            testService();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}