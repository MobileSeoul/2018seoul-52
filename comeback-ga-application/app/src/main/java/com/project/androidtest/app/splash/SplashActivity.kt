package com.project.androidtest.app.splash

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.project.androidtest.R
import com.project.androidtest.app.base.BaseActivity
import com.project.androidtest.util.ACTION_GOTO_HOME
import com.project.androidtest.util.ACTION_GOTO_LOGIN
import com.project.androidtest.util.PREF_KEY_USER_ID
import com.project.androidtest.util.PreferenceHelper
import com.uber.autodispose.AutoDispose
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_main)

        Observable.just(Object())
                .delay(3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .`as`(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe {
                    var userId = PreferenceHelper.getInstance(this).getIntPreference(PREF_KEY_USER_ID)

                    if (userId >= 0) {
                        Intent(ACTION_GOTO_HOME).run {
                            startActivity(this)
                        }
                    } else {
                        Intent(ACTION_GOTO_LOGIN).run {
                            startActivity(this)
                        }
                    }
                    finish()
                }
    }
}
