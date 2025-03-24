package com.peihua.alarmdemo

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Handler
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.peihua.alarmdemo.utils.dLog
import com.peihua.alarmdemo.utils.eLog

class AlarmReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context, intent: Intent?) {
        dLog { "alarmClockInfo>>>>AlarmReceiver" }
        val notificationIntent = Intent(context, AlarmService::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        try {
            context.startService(notificationIntent)
//            context?.startActivity(notificationIntent)
            showNotification(context, "AlarmReceiver", "AlarmReceiver")
            dLog { "MyAlarmReceiver>>alarmClockInfo>Activity started successfully" }
        } catch (e: Exception) {
            eLog { "MyAlarmReceiver>>alarmClockInfo>Failed to start Activity: ${e.message}" }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun showNotification(context: Context, title: String, message: String) {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val paddingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        // 创建通知
      val manager=  context.getSystemService(NotificationManager::class.java)!!
        //创建通道
        if (manager.getNotificationChannel("default") == null) {
            manager.createNotificationChannel(
                NotificationChannel(
                    "default",
                    "default",
                    NotificationManager.IMPORTANCE_HIGH
                )
            )
        }
        val builder = NotificationCompat.Builder(context, "default")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(paddingIntent)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(0, builder.build())
    }
}

class ReminderObserver(handler: Handler) : ContentObserver(handler) {
    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)

    }

    override fun onChange(selfChange: Boolean, uri: Uri?) {
        super.onChange(selfChange, uri)
        dLog { "ReminderObserver>>alarmClockInfo>Activity started successfully<<$uri" }
    }

    override fun onChange(selfChange: Boolean, uri: Uri?, flags: Int) {
        super.onChange(selfChange, uri, flags)
        dLog { "ReminderObserver>>alarmClockInfo>Activity started successfully<<flags:$flags,uri:$uri" }
    }
}