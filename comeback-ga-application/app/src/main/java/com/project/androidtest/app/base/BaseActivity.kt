package com.project.androidtest.app.base

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity

abstract class BaseActivity: AppCompatActivity() {
    private var frames: MutableSet<BaseFrame> = mutableSetOf()

    protected fun addFrame(frame: BaseFrame) {
        lifecycle.addObserver(frame)
        frames.add(frame)
    }

    protected fun removeFrame(frame: BaseFrame) {
        lifecycle.removeObserver(frame)
        frames.remove(frame)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }

    override fun onDestroy() {
        super.onDestroy()
        frames.forEach {
            lifecycle.removeObserver(it)
        }
        frames.clear()
    }
}