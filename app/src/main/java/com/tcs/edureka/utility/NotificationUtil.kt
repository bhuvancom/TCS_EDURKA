package com.tcs.edureka.utility

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.tcs.edureka.R

/**
 * @author Bhuvaneshvar
 */
class NotificationUtil {

    companion object {
        private const val CHANNEL_ID = "TCS GMap"
        private const val CHANNEL_ID_WITHOUT_SOUND = "TCS GMAP"
        private const val NOTIFICATION_CHANNEL = "TCS Gmap notification"

        /**
         * Method to instantiate notification
         */
        @JvmStatic
        fun init(activity: Activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationManager =
                        activity.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
                val existingChannel = notificationManager.getNotificationChannel(CHANNEL_ID)
                val anotherChangger = notificationManager.getNotificationChannel(CHANNEL_ID_WITHOUT_SOUND)
                if (existingChannel == null) {
                    // Create the NotificationChannel
                    val name = NOTIFICATION_CHANNEL
                    val importance = NotificationManager.IMPORTANCE_HIGH
                    val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
                    notificationManager.createNotificationChannel(mChannel)
                }
                if (anotherChangger == null) {
                    val name = NOTIFICATION_CHANNEL
                    val importance = NotificationManager.IMPORTANCE_LOW
                    val mChannel = NotificationChannel(CHANNEL_ID_WITHOUT_SOUND, "$name no sound", importance)
                    notificationManager.createNotificationChannel(mChannel)
                }
            }
        }

        /**
         * This method will invoke instant notification
         */
        @JvmStatic

        fun notify(title: String, msg: String, context: Context, priority: Int = 2) {
            val channel = if (priority == 1) CHANNEL_ID else CHANNEL_ID_WITHOUT_SOUND
            val notificationBuild = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle("TCS Gmap Monitor : $title")
                    .setContentText("Message : $msg")
                    .setChannelId(channel)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(true)
                    .setStyle(NotificationCompat.BigTextStyle())

            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(1, notificationBuild.build())
        }
    }
}