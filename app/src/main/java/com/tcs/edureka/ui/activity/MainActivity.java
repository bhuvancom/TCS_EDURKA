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
import com.tcs.edureka.ui.activity.contacts.ContactsActivity;
import com.tcs.edureka.ui.activity.map.MapActivity;
import com.tcs.edureka.utility.Constants;
import com.tcs.edureka.utility.NotificationUtil;

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

        NotificationUtil.init(this);


        binding.btnOpenFragment.setOnClickListener((v) ->
                openMapActivity("Bhinga", "Bahraich")
        );


        binding.btnOpenFragment2.setOnClickListener(v -> {
            openContacts();
        });
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