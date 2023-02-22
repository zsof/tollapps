/*
 * @author Prog-Oak Kft. <hello@prog-oak.com>
 * @copyright Copyright (c) 2021, Prog-Oak Kft.
 */
package hu.zsof.tollapps.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import hu.zsof.tollapps.MainActivity
import hu.zsof.tollapps.R

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Create the NotificationChannel
                val name = context.getString(R.string.notification_name)
                val descriptionText = context.getString(R.string.notification_details)
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val notificationChannel = NotificationChannel("NotifyId", name, importance)
                notificationChannel.description = descriptionText
                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(notificationChannel)
            }

            // Intent to open activity and that clear the notification
            val showIntent = Intent(context, MainActivity::class.java)

            val intentFlag =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
                } else {
                    PendingIntent.FLAG_CANCEL_CURRENT
                }

            val contentIntent = PendingIntent.getActivity(context, 0, showIntent, intentFlag)

            // Create the notification to be shown
            val builder = NotificationCompat.Builder(context, "NotifyId")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context.getString(R.string.notification_name))
                .setContentText(context.getString(R.string.notification_message))
                .setAutoCancel(true)
                .setContentIntent(contentIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            // Get the Notification manager service
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Generate an Id for each notification
            val id = System.currentTimeMillis() / 1000

            // Show a notification
            notificationManager.notify(id.toInt(), builder.build())
        }
    }
}
