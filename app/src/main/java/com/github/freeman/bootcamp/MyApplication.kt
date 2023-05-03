package com.github.freeman.bootcamp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * HiltAndroidApp needs to be specified on the application, otherwise we can not use daggerHilt
 */
@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
    }
}