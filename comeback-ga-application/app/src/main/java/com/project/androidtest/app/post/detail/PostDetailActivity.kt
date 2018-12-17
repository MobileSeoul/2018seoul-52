package com.project.androidtest.app.post.detail

import android.Manifest
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.project.androidtest.R
import com.project.androidtest.app.post.detail.adapter.CommentAdapter
import com.project.androidtest.databinding.PostDetailMainBinding
import com.project.androidtest.model.post.PostModel
import com.project.androidtest.util.INTENT_KEY_ITEM
import com.project.androidtest.vm.CommentViewModel
import kotlinx.android.synthetic.main.post_detail_main.*
import java.util.function.Consumer
import android.Manifest.permission
import android.Manifest.permission.CALL_PHONE
import android.support.v4.content.PermissionChecker
import android.util.Log
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.project.androidtest.util.SERVER_URL
import com.project.androidtest.util.checkPermissions
import java.util.ArrayList


class PostDetailActivity : AppCompatActivity() {
    private val TAG = PostDetailActivity::class.java.simpleName
    private lateinit var binding: PostDetailMainBinding
    private var viewModel: CommentViewModel? = null
    private var commentAdapter: CommentAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@PostDetailActivity, R.layout.post_detail_main)

        var postItem = intent.getSerializableExtra(INTENT_KEY_ITEM) as PostModel

        binding.post = postItem

        viewModel = ViewModelProviders.of(this as FragmentActivity).get(CommentViewModel::class.java).apply {
            getComments().observe(this@PostDetailActivity, Observer {
                if (it != null) {
                    commentAdapter?.setItems(it)
                }
            })
        }

        getCommentData(postItem.id)

        initCommentRecyclerView()

        initToolbar()

        btn_post_detail_comment.setOnClickListener {
            var comment = et_post_detail_comment.text.toString()
            viewModel?.createCommentData(this@PostDetailActivity, lifecycle, Consumer {  }, comment, postItem.id)
            et_post_detail_comment.setText("")
            Toast.makeText(this, "등록되었습니다.", Toast.LENGTH_SHORT).show()
        }

        btn_toolbar_call.setOnClickListener {
            Intent(Intent.ACTION_CALL).apply {
                data = Uri.parse("tel:${postItem.user.phone}")
                try {
                    startActivity(this)
                } catch (e: Exception) {
                    checkPermissions(arrayOf(Manifest.permission.CALL_PHONE))
                    e.printStackTrace()
                }
            }
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                var position = tab?.position
                layout_post_detail_01.visibility = View.GONE
                layout_post_detail_02.visibility = View.GONE

                when (position) {
                    0 -> layout_post_detail_01.visibility = View.VISIBLE
                    1 -> layout_post_detail_02.visibility = View.VISIBLE
                }
            }
        })

    }

    private fun initCommentRecyclerView() {
        commentAdapter = CommentAdapter()
        rv_post_detail_comment.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
            adapter = commentAdapter
        }
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        title = "포스트 상세보기"
        toolbar_title.text = toolbar.title
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_arrow_left)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    fun getCommentData(postId: Int) {
        viewModel?.getCommentsData(lifecycle, Consumer {  }, postId)
    }
}
