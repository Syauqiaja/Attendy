package com.hashtest.attendy

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class AttendyApp: Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        if(BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }
        Timber.d("App is running")
    }
}