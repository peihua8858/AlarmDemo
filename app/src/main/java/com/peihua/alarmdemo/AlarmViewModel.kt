package com.peihua.alarmdemo

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.CalendarContract
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.peihua.alarmdemo.utils.ResultData
import com.peihua.alarmdemo.utils.dLog
import com.peihua.alarmdemo.utils.request
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class AlarmViewModel : ViewModel() {
    var alarmState: MutableState<ResultData<String>> = mutableStateOf(ResultData.Starting())
    val eventsState: MutableState<ResultData<List<Event>>> = mutableStateOf(ResultData.Starting())
    val eventIds = mutableStateListOf<Long>()
    val MIN_TIME = 1742804972000
    val MAX_TIME = 1743897600000

    init {
        requestAlarm()
        requestEvents()
    }

    fun requestAlarm() {
        request(alarmState) {
            getAlarmData()
            ""
        }
    }

    fun requestEvents() {
        request(eventsState) {
            AlarmUtils.getAllEvents(AlarmApplication.instance)
                .filter { it.startTime > MIN_TIME && it.startTime < MAX_TIME }
                .sortedBy { it.startTime }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun getAlarmData() {
        val alarmManager = AlarmApplication.instance.getSystemService(AlarmManager::class.java)
        val alarmClockInfo = alarmManager.nextAlarmClock
        if (alarmClockInfo == null) {
            return
        }
        val format = "yyyy-MM-dd HH:mm:ss"
        val dateFormat = SimpleDateFormat(format, Locale.getDefault())
        val time = dateFormat.format(alarmClockInfo.triggerTime)
        dLog { "alarmClockInfo${time}" }
// 这是一个理论上的示例，实际中可能无法工作
        val contentResolver = AlarmApplication.instance.contentResolver;
//        val uri = Uri.parse("content://com.android.deskclock/alarms")
        val uri = Uri.parse("content://com.google.android.deskclock.provider/alarms")
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

    fun deleteAlarm() {
        val alarmManager = AlarmApplication.instance.getSystemService(AlarmManager::class.java)
        //添加一个闹钟
        val intent = Intent(AlarmApplication.instance, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            AlarmApplication.instance, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        //删除特定的闹钟
        alarmManager.cancel(pendingIntent)
//        alarmManager.cancel(pendingIntent)
    }

    @SuppressLint("ScheduleExactAlarm")
    fun addAlarm(context: Context) {
        val alarmManager = AlarmApplication.instance.getSystemService(AlarmManager::class.java)
        //添加一个闹钟
        val intent = Intent(context, AlarmReceiver::class.java)
//        val intent = Intent("android.intent.action.DEMO_ALARM_RECEIVER")
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setAlarmClock(
            AlarmManager.AlarmClockInfo(
                System.currentTimeMillis() + 60_000,
                pendingIntent
            ), pendingIntent
        )
        //判断闹钟是否添加成功
        val alarmClockInfo = alarmManager.nextAlarmClock
        dLog { "alarmClockInfo.triggerTime:${formatTime(alarmClockInfo.triggerTime)}" }
    }

    @SuppressLint("ScheduleExactAlarm")
    fun setAlarmForEvent(context: Context, eventStartTime: Long) {
        // 创建一个 Intent
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        // 使用 AlarmManager 设置一个闹钟
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // 设置 Alarm，提前 15 分钟提醒
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            eventStartTime - 15 * 60 * 1000,
            pendingIntent
        )
    }

    fun addEvent() {
        request(eventsState) {
//            val tempEventId = Random(1000).nextLong()
            val eventId =
                CalendarReminderUtils.addCalendarEvent(
                    AlarmApplication.instance,
//                    tempEventId,
                    "日程测试1111",
                    "description 日程测试1111",
                    System.currentTimeMillis() + 60_000,
                    0
//                    System.currentTimeMillis() + 180_000,
//                    10,
//                    true
                )
            dLog { "addEvent>>>添加日程${if (eventId != -1L) "成功" else "失败"}，eventId:$eventId" }
            if (eventId != -1L) {
                eventIds.add(eventId)
            } else {
                dLog { "addEvent>>>添加日程失败eventId:$eventId" }
            }
            AlarmUtils.getAllEvents(AlarmApplication.instance)//1743436800000
                .filter { it.startTime > MIN_TIME && it.startTime < MAX_TIME }
                .sortedBy { it.startTime }
        }
    }

    fun addEvent(title: String, description: String, startTime: Long, endTime: Long) {
        request(eventsState) {
            val eventId =
                CalendarReminderUtils.addCalendarEvent(
                    AlarmApplication.instance,
                    title,
                    description,
                    startTime,
                    0
//                    endTime,
//                    10,
//                    true
                )
            eventIds.add(eventId)
            AlarmUtils.getAllEvents(AlarmApplication.instance)
                .filter { it.startTime > MIN_TIME && it.startTime < MAX_TIME }
                .sortedBy { it.startTime }
        }
    }

    fun deleteEvent() {
        request(eventsState) {
            if (!eventIds.isEmpty()) {
                val eventId = eventIds.removeAt(0)
                CalendarReminderUtils.deleteCalendarEvent(AlarmApplication.instance, eventId)
            }
            AlarmUtils.getAllEvents(AlarmApplication.instance)
                .filter { it.startTime > MIN_TIME && it.startTime < MAX_TIME }
                .sortedBy { it.startTime }
        }

    }

    fun deleteEvent(eventId: Long) {
        request(eventsState) {
            val result =
                CalendarReminderUtils.deleteCalendarEvent(AlarmApplication.instance, eventId)
            dLog { "deleteEvent>>>result:$result" }
            AlarmUtils.getAllEvents(AlarmApplication.instance)
                .filter { it.startTime > MIN_TIME && it.startTime < MAX_TIME }
                .sortedBy { it.startTime }
        }

    }

}

object AlarmUtils {

    // 设置闹钟
    @SuppressLint("ScheduleExactAlarm")
    fun setAlarm(context: Context, alarmId: Int, triggerAtMillis: Long, alarmMessage: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("alarmMessage", alarmMessage)
        }

        // 创建一个唯一的 PendingIntent
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        // 设置闹钟
        alarmManager?.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
    }

    // 取消闹钟
    fun cancelAlarm(context: Context, alarmId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)

        // 创建同样的 PendingIntent
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        // 取消闹钟
        alarmManager?.cancel(pendingIntent)
    }

    fun getCalendarAccount(context: Context): Triple<Long, String?, String?>? {
        val projection = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.ACCOUNT_NAME,
            CalendarContract.Calendars.ACCOUNT_TYPE
        )

        val cursor = context.contentResolver.query(
            CalendarContract.Calendars.CONTENT_URI,
            projection,
            null,
            null,
            null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                val accountId = it.getLong(it.getColumnIndex(CalendarContract.Calendars._ID))
                val accountName =
                    it.getString(it.getColumnIndex(CalendarContract.Calendars.ACCOUNT_NAME))
                val accountType =
                    it.getString(it.getColumnIndex(CalendarContract.Calendars.ACCOUNT_TYPE))
                return Triple(accountId, accountName, accountType)
            }
        }
        return null
    }

    fun addEvent(
        context: Context,
        title: String,
        description: String,
        startTime: Long,
        endTime: Long,
    ): Long {
        val account = getCalendarAccount(context)
        if (account != null) {
            val values = ContentValues().apply {
                dLog { "CalendarEvent>>>Event added with CALENDAR_ID: ${account.first}" }
                put(CalendarContract.Events.CALENDAR_ID, account.first) //插入账户的id
//                put(CalendarContract.Events.ACCOUNT_NAME, account.second) // 使用有效的账户名称
//                put(CalendarContract.Events.ACCOUNT_TYPE, account.third) // 使用有效的账户类型
                put(CalendarContract.Events.TITLE, title)
                put(CalendarContract.Events.DESCRIPTION, description)
                put(CalendarContract.Events.DTSTART, startTime)
                put(CalendarContract.Events.DTEND, endTime)
                put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id) // 设置时区
                put(CalendarContract.Events.VISIBLE, 1) // 确保事件可见
            }

            // 插入日历事件
            val uri = context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
            val eventId = ContentUris.parseId(uri!!)
            val notificationUri =
                ContentUris.withAppendedId(
                    CalendarContract.Events.CONTENT_URI,
                    ContentUris.parseId(uri)
                )
            context.contentResolver.notifyChange(notificationUri, null)
            dLog { "CalendarEvent>>>Event added with ID: $eventId" }
            return eventId
        } else {
            dLog { "CalendarEvent>>>No calendar account available" }
            return 0L
        }
    }

    fun deleteEvent(context: Context, eventId: Long) {
        val deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId)
        context.contentResolver.delete(deleteUri, null, null)
    }

    fun getAllEvents(context: Context): List<Event> {
        val events = mutableListOf<Event>()
        val contentResolver = context.contentResolver

        // 查询日历事件
        val cursor: Cursor? = contentResolver.query(
            CalendarContract.Events.CONTENT_URI,
            null,
            null,
            null,
            null
        )

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getLong(it.getColumnIndex(CalendarContract.Events._ID))
                val title = it.getString(it.getColumnIndex(CalendarContract.Events.TITLE))
                val description =
                    it.getString(it.getColumnIndex(CalendarContract.Events.DESCRIPTION))
                val startTime = it.getLong(it.getColumnIndex(CalendarContract.Events.DTSTART))
                val endTime = it.getLong(it.getColumnIndex(CalendarContract.Events.DTEND))
                val eventTimezone =
                    it.getString(it.getColumnIndex(CalendarContract.Events.EVENT_TIMEZONE))

                val event = Event(id, title, description, startTime, endTime, eventTimezone)
                events.add(event)
            }
        }

        return events
    }
}

data class Event(
    val id: Long,
    val title: String,
    val description: String?,
    val startTime: Long,
    val endTime: Long,
    val eventTimezone: String?,
)