package com.project.androidtest.app.home.like

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.project.androidtest.app.base.BaseFrame
import com.project.androidtest.vm.PostViewModel
import com.project.androidtest.app.home.like.adapter.PostLikeAdapter
import com.project.androidtest.model.post.PostModel
import com.project.androidtest.util.ACTION_GOTO_POST_DETAIL
import com.project.androidtest.util.INTENT_KEY_ITEM
import kotlinx.android.synthetic.main.like_content.view.*
import java.util.function.Consumer

class LikeFrame(contentView: View, parentLifecycle: Lifecycle): BaseFrame(contentView, parentLifecycle) {
    private var viewModel: PostViewModel? = null
    private var likePostAdapter: PostLikeAdapter? = null


    init {
        viewModel = ViewModelProviders.of(context as FragmentActivity).get(PostViewModel::class.java).apply {
            getLikedPosts().observe(this@LikeFrame, Observer {
                if (it?.size != 0) {
                    it?.let { it1 -> likePostAdapter?.setItems(it1) }
                    contentView.view_like_empty.visibility = View.GONE
                } else {
                    contentView.view_like_empty.visibility = View.VISIBLE
                }
            })
        }

        getLikePostsData()

        initPostLikeRecyclerView()
    }

    private fun initPostLikeRecyclerView() {
        likePostAdapter = PostLikeAdapter()
        contentView.rv_like_post.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
            adapter = likePostAdapter
        }
        likePostAdapter?.setOnItemClickListener(object: PostLikeAdapter.OnItemClickListener{
            override fun onItemClick(item: PostModel) {
                Intent(ACTION_GOTO_POST_DETAIL).apply {
                    putExtra(INTENT_KEY_ITEM, item)
                    startActivity(this)
                }
            }
        })
    }

    fun getLikePostsData() {
        viewModel?.getLikedPostsData(context, lifecycle, Consumer {  })
    }
}