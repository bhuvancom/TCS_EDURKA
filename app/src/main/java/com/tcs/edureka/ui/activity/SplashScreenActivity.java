package com.tcs.edureka.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.tcs.edureka.R;

/**
 * @author Bhuvaneshvar & Suraj
 */
public class SplashScreenActivity extends AppCompatActivity {

    //variables
    Animation topAnim, bottomAnim;
    ImageView image;
    TextView logo, slogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);


        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animations);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animations);

        image = findViewById(R.id.imageView);
        logo = findViewById(R.id.textView);
        slogan = findViewById(R.id.textView2);

        image.setAnimation(topAnim);
        logo.setAnimation(bottomAnim);
        slogan.setAnimation(bottomAnim);

        int SPLASH_SCREEN = 1000;
        new Handler().postDelayed(this::openMain, SPLASH_SCREEN);

    }

    void openMain() {
        Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
        startActivity(intent);
        finishAffinity();
    }
}