package com.project.androidtest

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import com.facebook.drawee.backends.pipeline.Fresco

class AndroidTestApplication : Application() {
    companion object {
        private var instance : AndroidTestApplication? = null
        fun getInstance() = instance
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }

    override fun onCreate() {
        super.onCreate()
        initApplicationInstance()
        Fresco.initialize(this)
    }

    private fun initApplicationInstance() {
        instance = this@AndroidTestApplication
    }
}