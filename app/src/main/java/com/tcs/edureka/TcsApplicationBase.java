package com.tcs.edureka;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.tcs.edureka.utility.Constants;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class TcsApplicationBase extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    Constants.CHANNEL_ID,
                    Constants.CHANNEL_ID,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("High importance notification");
            NotificationChannel channel2 = new NotificationChannel(
                    Constants.CHANNEL_ID_WITHOUT_SOUND,
                    Constants.CHANNEL_ID_WITHOUT_SOUND,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel2.setDescription("Medium importance notification");
            NotificationChannel channel3 = new NotificationChannel(
                    Constants.CHANNEL_ID_STICKY,
                    Constants.CHANNEL_ID_STICKY,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel3.setDescription("High importance notification");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
            manager.createNotificationChannel(channel2);
            manager.createNotificationChannel(channel3);
        }
    }
}
