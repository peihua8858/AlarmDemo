package com.peihua.alarmdemo

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.Context
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.AlarmManagerCompat
import androidx.lifecycle.ViewModel
import com.peihua.alarmdemo.utils.ResultData
import com.peihua.alarmdemo.utils.dLog
import com.peihua.alarmdemo.utils.request

class AlarmViewModel : ViewModel() {
    var alarmState: MutableState<ResultData<String>> = mutableStateOf(ResultData.Starting())

    init {
        requestAlarm()
    }

    fun requestAlarm() {
        request(alarmState) {
            getAlarmData()
            ""
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun getAlarmData() {
        val alarmManager = AlarmApplication.instance.getSystemService(AlarmManager::class.java)
      val alarmClockInfo=  alarmManager.nextAlarmClock
        dLog { "alarmClockInfo${alarmClockInfo.triggerTime}" }
// 这是一个理论上的示例，实际中可能无法工作
        val contentResolver = AlarmApplication.instance.contentResolver;
        val uri = Uri.parse("content://com.google.android.deskclock.provider");
        // 查询闹钟信息
        val cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                if (cursor.moveToFirst()) {
                    do {
                        for (i in 0 until cursor.columnCount) {
                            val alarmId = cursor.getLong(i);
                            val columnName = cursor.getColumnName(i);
                            val columnValue = cursor.getString(i);
                            dLog { "Column ID: $alarmId, Name: $columnName, Value: $columnValue" }
                        }
                    } while (cursor.moveToNext());
                }
            }
            cursor.close();
        }
    }
}