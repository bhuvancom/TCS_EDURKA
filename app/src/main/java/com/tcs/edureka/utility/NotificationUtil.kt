package com.tcs.edureka.utility

import android.app.PendingIntent
import android.content.Context
import android.graphics.Color
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.tcs.edureka.R

/**
 * @author Bhuvaneshvar
 */
class NotificationUtil {

    companion object {
        /**
         * Method to instantiate notification
         */
//        @JvmStatic
//        fun init(activity: Activity) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                val notificationManager =
//                        activity.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
//                val existingChannel = notificationManager.getNotificationChannel(CHANNEL_ID)
//                val anotherChangger = notificationManager.getNotificationChannel(CHANNEL_ID_WITHOUT_SOUND)
//                if (existingChannel == null) {
//                    // Create the NotificationChannel
//                    val name = NOTIFICATION_CHANNEL
//                    val importance = NotificationManager.IMPORTANCE_HIGH
//                    val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
//                    notificationManager.createNotificationChannel(mChannel)
//                }
//                if (anotherChangger == null) {
//                    val name = NOTIFICATION_CHANNEL
//                    val importance = NotificationManager.IMPORTANCE_LOW
//                    val mChannel = NotificationChannel(CHANNEL_ID_WITHOUT_SOUND, "$name no sound", importance)
//                    notificationManager.createNotificationChannel(mChannel)
//                }
//            }
//        }

        /**
         * This method will invoke instant notification
         * @param priority if 1 then with sound, if 2 without sound
         */
        @JvmStatic

        fun notify(title: String, msg: String, context: Context, priority: Int = 2) {
            val channel = if (priority == 1) Constants.CHANNEL_ID else Constants.CHANNEL_ID_WITHOUT_SOUND
            val notificationBuild = NotificationCompat.Builder(context, Constants.CHANNEL_ID)
                    .setContentTitle("TCS : $title")
                    .setContentText("Message : $msg")
                    .setChannelId(if (priority == 3) (Constants.CHANNEL_ID_STICKY) else (channel))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(priority != 3)
                    .setStyle(NotificationCompat.BigTextStyle())
            val notificationManager = NotificationManagerCompat.from(context)
            val build = notificationBuild.build()

            notificationManager.notify(priority, build)
        }

        @JvmStatic
        fun notifyWithIntent(title: String, msg: String, context: Context, intent: PendingIntent) {
            val channel = Constants.CHANNEL_ID_STICKY
            val notificationBuild = NotificationCompat.Builder(context, Constants.CHANNEL_ID)
                    .setContentTitle("TCS : $title")
                    .setContentText("Message : $msg")
                    .setChannelId(channel)
                    .setContentIntent(intent)
                    .setOngoing(true)
                    .setColor(Color.BLUE)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .addAction(R.drawable.ic_launcher_foreground, "Cancel", intent)
                    .setAutoCancel(true)
                    .setStyle(NotificationCompat.BigTextStyle())
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(3, notificationBuild.build())
        }
    }
}