package com.project.androidtest.util

import android.annotation.SuppressLint
import android.support.design.internal.BottomNavigationItemView
import android.support.design.widget.BottomNavigationView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.lang.reflect.Field
import android.support.design.internal.BottomNavigationMenuView



@SuppressLint("RestrictedApi")
fun disableShiftMode(view: BottomNavigationView) {
    val menuView = view.getChildAt(0) as BottomNavigationMenuView

    try {
        menuView.javaClass.getDeclaredField("mShiftingMode").apply {
            isAccessible = true
            setBoolean(menuView, false)
            isAccessible = false
        }

        Observable.range(0, menuView.childCount)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val item = menuView.getChildAt(it) as BottomNavigationItemView
                    item.apply {
                        setShiftingMode(false)
                        setChecked(item.itemData.isChecked)
                    }
                })
    } catch (e: NoSuchFieldException) {
        e.printStackTrace()
    } catch (e: IllegalAccessException) {
        e.printStackTrace()
    }
}