package com.peihua.alarmdemo

class AlarmApplication : android.app.Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
    companion object{
        private const val TAG = "AlarmApplication"
        lateinit var instance: AlarmApplication
    }
}