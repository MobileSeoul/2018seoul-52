package com.project.androidtest.app.home.more

import android.app.Activity
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.project.androidtest.app.base.BaseFrame
import com.project.androidtest.vm.PostViewModel
import com.project.androidtest.app.home.more.adapter.MyPostAdapter
import com.project.androidtest.model.post.PostModel
import com.project.androidtest.util.*
import com.project.androidtest.vm.UserViewModel
import kotlinx.android.synthetic.main.more_content.view.*
import java.util.function.Consumer


class MoreFrame(contentView: View, parentLifecycle: Lifecycle): BaseFrame(contentView, parentLifecycle) {
    private var postViewModel: PostViewModel? = null
    private var userViewModel: UserViewModel? = null
    private var myPostsAdapter: MyPostAdapter? = null

    init {
        postViewModel = ViewModelProviders.of(context as FragmentActivity).get(PostViewModel::class.java).apply {
            getMyPosts().observe(this@MoreFrame, Observer {
                if (it?.size != 0) {
                    it?.let { it1 -> myPostsAdapter?.setItems(it1) }
                    contentView.view_more_empty_posts.visibility = View.INVISIBLE
                } else {
                    contentView.view_more_empty_posts.visibility = View.VISIBLE
                }
            })
        }
        userViewModel = ViewModelProviders.of(context as FragmentActivity).get(UserViewModel::class.java).apply {
            getUser().observe(this@MoreFrame, Observer {
                with (contentView) {
                    iv_more_profile_img.setImageURI(it?.poster)
                    tv_more_profile_nickname.text = it?.nickname
                    tv_more_profile_user.text = it?.user
                }
            })
        }

        getMyPostsData()

        getUserData()

        initMyPostRecyclerView()

        contentView.btn_more_logout.setOnClickListener {
            PreferenceHelper.getInstance(context).setIntPreference(PREF_KEY_USER_ID, -1)
            startActivity(Intent(ACTION_GOTO_LOGIN))
            (context as Activity).finish()
        }

        contentView.btn_more_profile_edit.setOnClickListener {
            startActivity(Intent(ACTION_GOTO_MY_PROFILE))
        }

        contentView.btn_more_help.setOnClickListener {
            startActivity(Intent(ACTION_GOTO_HELP))
        }
    }

    private fun initMyPostRecyclerView() {
        myPostsAdapter = MyPostAdapter()
        contentView.rv_more_my_post.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
            adapter = myPostsAdapter
        }
        myPostsAdapter?.setOnItemClickListener(object : MyPostAdapter.OnItemClickListener{
            override fun onItemClick(item: PostModel) {
                Intent(ACTION_GOTO_POST_UPDATE).apply {
                    putExtra(INTENT_KEY_ITEM, item)
                    startActivity(this)
                }
            }
        })
    }

    fun getMyPostsData() {
        postViewModel?.getMyPostsData(lifecycle, Consumer {  },  PreferenceHelper.getInstance(context).getIntPreference(PREF_KEY_USER_ID))
    }

    fun getUserData() {
        userViewModel?.getUserData(lifecycle, Consumer {  }, PreferenceHelper.getInstance(context).getIntPreference(PREF_KEY_USER_ID))
    }
}