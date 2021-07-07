package com.tcs.edureka.receivers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.tcs.edureka.R;
import com.tcs.edureka.ui.activity.appointments.AppointmentActivity;
import com.tcs.edureka.ui.activity.appointments.RemindersActivity;
import com.tcs.edureka.ui.activity.map.MapActivity;
import com.tcs.edureka.ui.activity.media.MyMediaPlayerActivity;
import com.tcs.edureka.ui.activity.weather.WeatherActivity;
import com.tcs.edureka.utility.Constants;

/*
 * @author Bhavya Bhanu
 */
public class SpeechRecognitionReceiver extends BroadcastReceiver {

    Intent intent;

    @Override
    public void onReceive(Context context, Intent receiverIntent) {
        if (receiverIntent.getAction().equals(Constants.BROADCAST_ACTION_SPEECH_RECOGNITION)) {
            Log.d(Constants.TAG_SPEECH_RECOGNIZER, "received basic command");
            Bundle bundle = receiverIntent.getExtras();
            if (bundle != null) {
                if (bundle.getBoolean(Constants.SPEECH_RECOGNITION_COMMAND_IDENTIFIED)) {
                    switch (bundle.getString(Constants.COMMAND)) {

                        case Constants.SPEECH_RECOGNITION_COMMAND_APPOINTMENTS:
                            if (bundle.size() > 2) {
                                intent = new Intent(context, AppointmentActivity.class);
                            } else {
                                intent = new Intent(context, RemindersActivity.class);
                            }
                            intent.putExtras(receiverIntent.getExtras());
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            invokeNotification(context, intent, Constants.SPEECH_RECOGNITION_COMMAND_APPOINTMENTS.toUpperCase(), true);
                            //context.startActivity(appointmentIntent);
                            break;
                        case Constants.SPEECH_RECOGNITION_COMMAND_CALL:
                            //Call.dialCall(context, "");
                            break;
                        case Constants.SPEECH_RECOGNITION_COMMAND_CALL_HI:
                            break;
                        case Constants.SPEECH_RECOGNITION_COMMAND_CALL_BYE:
                            break;
                        case Constants.SPEECH_RECOGNITION_COMMAND_MUSIC_PLAYER_PLAY:
                            musicControl(context, Constants.MUSIC_ACTION_PLAY);
                            break;
                        case Constants.SPEECH_RECOGNITION_COMMAND_MUSIC_PLAYER_NEXT:
                            musicControl(context, Constants.MUSIC_ACTION_NEXT);
                            break;
                        case Constants.SPEECH_RECOGNITION_COMMAND_MUSIC_PLAYER_PAUSE:
                        case Constants.SPEECH_RECOGNITION_COMMAND_MUSIC_PLAYER_STOP:
                            musicControl(context, Constants.MUSIC_ACTION_PAUSE);
                            break;
                        case Constants.SPEECH_RECOGNITION_COMMAND_MUSIC_PLAYER_PREVIOUS:
                            musicControl(context, Constants.MUSIC_ACTION_PREVIOUS);
                            break;
                        case Constants.SPEECH_RECOGNITION_COMMAND_MAP:
                            intent = new Intent(context, MapActivity.class);
                            intent.putExtras(receiverIntent.getExtras());
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                            invokeNotification(context, intent, Constants.SPEECH_RECOGNITION_COMMAND_MAP.toUpperCase(), true);
                            break;

                        case Constants.SPEECH_RECOGNITION_COMMAND_WEATHER:
                            intent = new Intent(context, WeatherActivity.class);
                            intent.putExtras(receiverIntent.getExtras());
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                            invokeNotification(context, intent, Constants.SPEECH_RECOGNITION_COMMAND_WEATHER.toUpperCase(), true);
                            break;

                        default:
                            break;
                    }
                } else {
                    Log.d(Constants.TAG_SPEECH_RECOGNIZER, "Command not recognized");
                    invokeNotification(context, null, "Command not identifed", false);

                }
            }
        }
    }

    private void musicControl(Context context, String action) {
        Intent intent = new Intent(context, MyMediaPlayerActivity.class);
        intent.putExtra(Constants.MUSIC_ACTION, action);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void invokeNotification(Context context, Intent intent, String title, boolean actionRequired) {
        String channelID = Constants.CHANNEL_ID;
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channelID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(Color.BLUE)
                .setStyle(new NotificationCompat.BigTextStyle());

        if (actionRequired) {
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            notificationBuilder.setContentText("Action for " + title)
                    .setFullScreenIntent(pendingIntent, true)
                    .addAction(R.drawable.ic_launcher_foreground, "Take me there", pendingIntent);
        }
        Notification notification = notificationBuilder.build();

        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager.getNotificationChannel(channelID) == null) {
            notificationManager.createNotificationChannel(new NotificationChannel(channelID, "Channel", NotificationManager.IMPORTANCE_HIGH));
        }
        notificationManager.notify(1, notification);
    }
}
