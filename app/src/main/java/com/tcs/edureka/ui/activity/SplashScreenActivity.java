package com.tcs.edureka.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.tcs.edureka.R;

/**
 * @author Bhuvaneshvar
 */
public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    sleep(1000L);
                    openMain();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    openMain();
                }
            }
        };

        thread.start();

    }

    void openMain() {
        Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
        startActivity(intent);
        finishAffinity();
    }
}