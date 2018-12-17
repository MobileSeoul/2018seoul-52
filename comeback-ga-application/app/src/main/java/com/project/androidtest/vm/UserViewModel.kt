package com.project.androidtest.vm

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.project.androidtest.model.user.UserModel
import com.project.androidtest.rest.ServerClient
import com.project.androidtest.rest.user.UserService
import com.project.androidtest.util.SERVER_URL
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.function.Consumer

class UserViewModel: ViewModel() {
    private var user = MutableLiveData<UserModel>()

    fun getUser(): LiveData<UserModel> = user

    fun getUserData(lifecycle: Lifecycle, errorConsumer: Consumer<Throwable>, userId: Int) {
        ServerClient.getOrCreate(UserService::class.java)
                .getUserProfile(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it.poster = "$SERVER_URL${it.poster}"
                    user.postValue(it)
                }, {
                    errorConsumer
                })
    }
}