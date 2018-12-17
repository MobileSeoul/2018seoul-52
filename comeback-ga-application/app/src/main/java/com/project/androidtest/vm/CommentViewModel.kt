package com.project.androidtest.vm

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import com.project.androidtest.model.comment.CommentModel
import com.project.androidtest.rest.ServerClient
import com.project.androidtest.rest.post.PostService
import com.project.androidtest.rest.user.UserService
import com.project.androidtest.util.PREF_KEY_USER_ID
import com.project.androidtest.util.PreferenceHelper
import com.project.androidtest.util.SERVER_URL
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.function.Consumer

class CommentViewModel: ViewModel() {
    private var comments = MutableLiveData<MutableList<CommentModel>>()

    fun getComments(): LiveData<MutableList<CommentModel>> = comments

    fun getCommentsData(lifecycle: Lifecycle, errorConsumer: Consumer<Throwable>, postId: Int) {
        ServerClient.getOrCreate(PostService::class.java)
                .getCommentList(postId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it.forEach {
                        it.user.poster = "$SERVER_URL${it.user.poster}"
                    }
                    comments.postValue(it)
                }, {
                    errorConsumer
                })
    }

    fun createCommentData(context: Context, lifecycle: Lifecycle, errorConsumer: Consumer<Throwable>, comment: String, postId: Int) {
        ServerClient.getOrCreate(PostService::class.java)
                .createComment(postId, PreferenceHelper.getInstance(context).getIntPreference(PREF_KEY_USER_ID), comment)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    ServerClient.getOrCreate(UserService::class.java)
                            .createCommentLike(PreferenceHelper.getInstance(context).getIntPreference(PREF_KEY_USER_ID), postId)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                getCommentsData(lifecycle, errorConsumer, postId)
                            })
                }, {
                })
    }
}