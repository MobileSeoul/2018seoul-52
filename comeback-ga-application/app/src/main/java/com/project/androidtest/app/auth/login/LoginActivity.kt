package com.project.androidtest.app.auth.login

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.project.androidtest.R
import com.project.androidtest.rest.ServerClient
import com.project.androidtest.rest.user.UserService
import com.project.androidtest.util.ACTION_GOTO_HOME
import com.project.androidtest.util.ACTION_GOTO_SIGN_UP
import com.project.androidtest.util.PREF_KEY_USER_ID
import com.project.androidtest.util.PreferenceHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.login_main.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_main)

        btn_login_login.setOnClickListener {
            var user = et_login_id.text.toString()
            var password = et_login_password.text.toString()

            ServerClient.getOrCreate(UserService::class.java)
                    .authUser(user, password)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        PreferenceHelper.getInstance(this@LoginActivity).setIntPreference(PREF_KEY_USER_ID, it.id)
                        startActivity(Intent(ACTION_GOTO_HOME))
                        finish()
                    },{
                        Toast.makeText(this@LoginActivity, "아이디/비밀번호가 맞지 않습니다.", Toast.LENGTH_SHORT).show()
                    })
        }

        btn_login_sign_up.setOnClickListener {
            startActivity(Intent(ACTION_GOTO_SIGN_UP))
        }
    }
}
