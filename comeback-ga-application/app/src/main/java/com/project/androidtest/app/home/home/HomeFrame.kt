package com.project.androidtest.app.home.home

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Color
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import com.project.androidtest.R
import com.project.androidtest.app.base.BaseFrame
import com.project.androidtest.vm.PostViewModel
import com.project.androidtest.app.home.home.adapter.PostAdapter
import com.project.androidtest.model.post.PostModel
import com.project.androidtest.util.ACTION_GOTO_POST_DETAIL
import com.project.androidtest.util.INTENT_KEY_ITEM
import kotlinx.android.synthetic.main.home_content.view.*
import java.util.function.Consumer

class HomeFrame(contentView: View, parentLifecycle: Lifecycle): BaseFrame(contentView, parentLifecycle) {
    private var postAdapter: PostAdapter? = null
    private var viewModel: PostViewModel? = null

    init {
        viewModel = ViewModelProviders.of(context as FragmentActivity).get(PostViewModel::class.java).apply {
            getPosts().observe(this@HomeFrame, Observer {
                if (it != null) {
                    postAdapter?.setItems(it)
                }
            })
        }

        getPostData()

        initPostRecyclerView()

        contentView.fab_home_content_up.setOnClickListener {
            contentView.rv_home_content_posts.smoothScrollToPosition(0)
        }

        contentView.btn_home_search.setOnClickListener {
            var query = contentView.et_btn_search.text.toString()
            postAdapter?.searchItems(query)
        }

        contentView.btn_home_filter.setOnClickListener {
            val categoryList = arrayOf("전체", "책임분양", "찾습니다", "찾았습니다")

            var customTitle = TextView(context).apply {
                text = "카테고리 선택"
                setPadding(32, 32, 32, 32)
                textSize = 16f
                setBackgroundColor(resources.getColor(R.color.colorPrimary))
                setTextColor(Color.WHITE)
            }

            AlertDialog.Builder(context)
                    .setCustomTitle(customTitle)
                    .setItems(categoryList) { p0, p1 ->
                        when (p1) {
                            0 -> postAdapter?.categoryFilter("")
                            1 -> postAdapter?.categoryFilter(categoryList[p1])
                            2 -> postAdapter?.categoryFilter(categoryList[p1])
                            3 -> postAdapter?.categoryFilter(categoryList[p1])
                        }
                    }.show()
        }

        contentView.et_btn_search.setOnKeyListener { p0, p1, event ->
            when (event.action) {
                KeyEvent.ACTION_DOWN and KeyEvent.KEYCODE_ENTER-> {
                    var query = contentView.et_btn_search.text.toString()
                    postAdapter?.searchItems(query)
                    true
                }
            }
            false
        }
    }

    private fun initPostRecyclerView() {
        postAdapter = PostAdapter()
        postAdapter?.setOnItemClickListener(object: PostAdapter.OnItemClickListener{
            override fun onItemClick(item: PostModel) {
                Intent(ACTION_GOTO_POST_DETAIL).apply {
                    putExtra(INTENT_KEY_ITEM, item)
                    startActivity(this)
                }
            }
        })
        contentView.rv_home_content_posts.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
            adapter = postAdapter
        }
        contentView.rv_home_content_posts.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!contentView.rv_home_content_posts.canScrollVertically(-1)) {
                    contentView.fab_home_content_up.visibility = View.GONE
                    contentView.layout_home_header.visibility = View.VISIBLE
                } else {
                    contentView.fab_home_content_up.visibility = View.VISIBLE
                    contentView.layout_home_header.visibility = View.GONE
                }
            }
        })
    }

    fun getPostData() {
        viewModel?.getPostData(lifecycle, Consumer {  })
    }
}