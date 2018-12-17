package com.project.androidtest.vm

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import com.project.androidtest.model.like.LikeModel
import com.project.androidtest.model.post.PostModel
import com.project.androidtest.model.user.UserModel
import com.project.androidtest.rest.ServerClient
import com.project.androidtest.rest.post.PostService
import com.project.androidtest.rest.user.UserService
import com.project.androidtest.util.PREF_KEY_USER_ID
import com.project.androidtest.util.PreferenceHelper
import com.project.androidtest.util.SERVER_URL
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.function.Consumer

class PostViewModel: ViewModel() {
    private var posts = MutableLiveData<MutableList<PostModel>>()
    private var likedPosts = MutableLiveData<MutableList<LikeModel>>()
    private var userPosts = MutableLiveData<MutableList<PostModel>>()

    fun getPosts(): LiveData<MutableList<PostModel>> = posts

    fun getPostData(lifecycle: Lifecycle, errorConsumer: Consumer<Throwable>) {
        ServerClient.getOrCreate(PostService::class.java)
                .getPostList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it.forEach {
                        it.poster = "$SERVER_URL${it.poster}"
                        it.user.poster = "$SERVER_URL${it.user.poster}"
                    }
                    posts.postValue(it)
                }, {
                   errorConsumer
                })
    }

    fun getLikedPosts(): LiveData<MutableList<LikeModel>> = likedPosts

    fun getLikedPostsData(context: Context, lifecycle: Lifecycle, errorConsumer: Consumer<Throwable>) {
        ServerClient.getOrCreate(UserService::class.java)
                .getLikedPosts(PreferenceHelper.getInstance(context).getIntPreference(PREF_KEY_USER_ID))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it.forEach {
                        it.post.poster = "$SERVER_URL${it.post.poster}"
                        it.post.user.poster = "$SERVER_URL${it.post.user.poster}"
                    }
                    likedPosts.postValue(it)
                }, {
                    errorConsumer
                })
    }

    fun getMyPosts(): LiveData<MutableList<PostModel>> = userPosts

    fun getMyPostsData(lifecycle: Lifecycle, errorConsumer: Consumer<Throwable>, userId: Int) {
        ServerClient.getOrCreate(UserService::class.java)
                .getUserPosts(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it.forEach {
                        it.poster = "$SERVER_URL${it.poster}"
                    }
                    userPosts.postValue(it)
                }, {
                    errorConsumer
                })
    }
}