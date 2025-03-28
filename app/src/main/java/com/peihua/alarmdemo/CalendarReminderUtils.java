package com.peihua.alarmdemo;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.CalendarContract;
import android.text.TextUtils;

import com.peihua.alarmdemo.utils.Logcat;

import java.util.Calendar;
import java.util.TimeZone;

public class CalendarReminderUtils {

    private static Uri CALENDER_URL = CalendarContract.Calendars.CONTENT_URI;
    private static Uri CALENDER_EVENT_URL = CalendarContract.Events.CONTENT_URI;
    private static Uri CALENDER_REMINDER_URL = CalendarContract.Reminders.CONTENT_URI;

    private static String CALENDARS_NAME = "AlarmDemo";
    private static String CALENDARS_ACCOUNT_NAME = "AlarmDemo";
    private static String CALENDARS_ACCOUNT_TYPE = "com.peihua.alarmdemo";
    private static String CALENDARS_DISPLAY_NAME = "AlarmDemo";
    private static boolean isCustomAccount = false;

    /**
     * 检查是否已经添加了日历账户，如果没有添加先添加一个日历账户再查询
     * 获取账户成功返回账户id，否则返回-1
     */
    private static int checkAndAddCalendarAccount(Context context) {
        int oldId = checkCalendarAccount(context);
        if (oldId >= 0) {
            return oldId;
        } else {
            long addId = addCalendarAccount(context);
            if (addId >= 0) {
                return checkCalendarAccount(context);
            } else {
                return -1;
            }
        }
    }

    /**
     * 检查是否存在现有账户，存在则返回账户id，否则返回-1
     */
    private static int checkCalendarAccount(Context context) {
        Cursor userCursor = context.getContentResolver().query(CALENDER_URL, null, null, null, null);
        try {
            if (userCursor == null) { //查询返回空值
                return -1;
            }
            int count = userCursor.getCount();
            if (count > 0) { //存在现有账户，取第一个账户的id返回
                if (isCustomAccount) {
                    userCursor.moveToFirst();
                    do {
                        String type =
                                userCursor.getString(userCursor.getColumnIndex(CalendarContract.Calendars.ACCOUNT_TYPE));
                        if (type.equals(CALENDARS_ACCOUNT_TYPE)) {
                            return userCursor.getInt(userCursor.getColumnIndex(CalendarContract.Calendars._ID));
                        }
                    } while (userCursor.moveToNext());
                    return -1;
                } else {
                    userCursor.moveToFirst();
                    int id = userCursor.getInt(userCursor.getColumnIndex(CalendarContract.Calendars._ID));
                    Logcat.d("checkCalendarAccount calId " + id);
                    return id;
                }
            } else {
                return -1;
            }
        } finally {
            if (userCursor != null) {
                userCursor.close();
            }
        }
    }

    /**
     * 添加日历账户，账户创建成功则返回账户id，否则返回-1
     */
    private static long addCalendarAccount(Context context) {
        TimeZone timeZone = TimeZone.getDefault();
        ContentValues value = new ContentValues();
        value.put(CalendarContract.Calendars.NAME, CALENDARS_NAME);
        value.put(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME);
        value.put(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE);
        value.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CALENDARS_DISPLAY_NAME);
        value.put(CalendarContract.Calendars.VISIBLE, 1);
        value.put(CalendarContract.Calendars.CALENDAR_COLOR, Color.BLUE);
        value.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
        value.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
        value.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, timeZone.getID());
        value.put(CalendarContract.Calendars.OWNER_ACCOUNT, CALENDARS_ACCOUNT_NAME);
        value.put(CalendarContract.Calendars.CAN_ORGANIZER_RESPOND, 0);

        Uri calendarUri = CALENDER_URL;
        calendarUri = calendarUri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE)
                .build();

        Uri result = context.getContentResolver().insert(calendarUri, value);
        return result == null ? -1 : ContentUris.parseId(result);
    }

    /**
     * 添加日历事件
     */
    public static long addCalendarEvent(Context context, String title, String description, long reminderTime, int reminderMinutes) {
        if (context == null) {
            return -1;
        }
        int calId = checkAndAddCalendarAccount(context); //获取日历账户的id
        if (calId < 0) { //获取账户id失败直接返回，添加日历事件失败
            return -1;
        }

        //添加日历事件
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(reminderTime);//设置开始时间
        long start = mCalendar.getTime().getTime();
//        mCalendar.setTimeInMillis(start + 10 * 60 * 1000);//设置终止时间，开始时间加10分钟
        long end = mCalendar.getTime().getTime();
        ContentValues event = new ContentValues();
        event.put(CalendarContract.Events.TITLE, title);
        event.put(CalendarContract.Events.DESCRIPTION, description);
        event.put(CalendarContract.Events.CALENDAR_ID, calId); //插入账户的id
        event.put(CalendarContract.Events.DTSTART, start);
        event.put(CalendarContract.Events.DTEND, end);
        event.put(CalendarContract.Events.HAS_ALARM, 1);//设置有闹钟提醒
        event.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());//这个是时区，必须有
        Uri newEvent = context.getContentResolver().insert(CALENDER_EVENT_URL, event); //添加事件
        if (newEvent == null) { //添加日历事件失败直接返回
            return -1;
        }

        //事件提醒的设定
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Reminders.EVENT_ID, ContentUris.parseId(newEvent));
        values.put(CalendarContract.Reminders.MINUTES,reminderMinutes);// 提前previousDate天有提醒
        values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        Uri uri = context.getContentResolver().insert(CALENDER_REMINDER_URL, values);
        if (uri == null) { //添加事件提醒失败直接返回
            return -1;
        }
        setAlarmForEvent(context, start, reminderMinutes);
        return ContentUris.parseId(newEvent);
    }

    @SuppressLint("ScheduleExactAlarm")
    private static void setAlarmForEvent(Context context, long eventStartTime, int reminderMinutes) {
        long alarmTime = eventStartTime - (reminderMinutes); // 提前提醒的时间

        Intent intent = new Intent(context, AlarmReceiver.class); // 指向广播接收器
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // 设置闹钟
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
    }
    /**
     * 删除日历事件
     */
    public  static  int deleteCalendarEvent(Context context, long delEventID) {
        if (context == null) {
            return -1;
        }
        Uri deleteUri =
                ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, delEventID);
        return context.getContentResolver().delete(deleteUri, null, null);
    }
    /**
     * 删除日历事件
     */
    public static void deleteCalendarEvent(Context context, String title) {
        if (context == null) {
            return;
        }
        Cursor eventCursor = context.getContentResolver().query(CALENDER_EVENT_URL, null, null, null, null);
        try {
            if (eventCursor == null) { //查询返回空值
                return;
            }
            if (eventCursor.getCount() > 0) {
                //遍历所有事件，找到title跟需要查询的title一样的项
                for (eventCursor.moveToFirst(); !eventCursor.isAfterLast(); eventCursor.moveToNext()) {
                    String eventTitle = eventCursor.getString(eventCursor.getColumnIndex("title"));
                    if (!TextUtils.isEmpty(title) && title.equals(eventTitle)) {
                        int id = eventCursor.getInt(eventCursor.getColumnIndex(CalendarContract.Calendars._ID));//取得id
                        Uri deleteUri = ContentUris.withAppendedId(CALENDER_EVENT_URL, id);
                        int rows = context.getContentResolver().delete(deleteUri, null, null);
                        if (rows == -1) { //事件删除失败
                            return;
                        }
                    }
                }
            }
        } finally {
            if (eventCursor != null) {
                eventCursor.close();
            }
        }
    }
}
