package com.peihua.alarmdemo

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.peihua.alarmdemo.utils.dLog

class AlarmService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        dLog { "alarmClockInfo>>>>AlarmService onCreate" }
        val notificationIntent = Intent(this, SecondActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(notificationIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        dLog { "alarmClockInfo>>>>AlarmService onDestroy" }
    }
}