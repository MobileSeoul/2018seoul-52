package com.project.androidtest.app.home

import android.Manifest
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.TextView
import com.project.androidtest.R
import com.project.androidtest.app.base.BaseActivity
import com.project.androidtest.app.home.home.HomeFrame
import com.project.androidtest.app.home.like.LikeFrame
import com.project.androidtest.app.home.more.MoreFrame
import com.project.androidtest.app.home.new.NewFrame
import com.project.androidtest.util.disableShiftMode
import kotlinx.android.synthetic.main.home_main.*
import com.project.androidtest.util.checkPermissions


class HomeActivity : BaseActivity() {
    private var selectedBottomMenu = 0

    private val homeFrame: HomeFrame by lazy {
        HomeFrame(layout_home, lifecycle)
    }

    private val newFrame: NewFrame by lazy {
        NewFrame(layout_new, lifecycle)
    }

    private val likeFrame: LikeFrame by lazy {
        LikeFrame(layout_like, lifecycle)
    }

    private val moreFrame: MoreFrame by lazy {
        MoreFrame(layout_more, lifecycle)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_main)

        checkPermissions(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.INTERNET
                )
        )

        initToolbar()

        initBottomNavigationView()

        addFrame(homeFrame)
        addFrame(newFrame)
        addFrame(likeFrame)
        addFrame(moreFrame)
    }

    override fun onResume() {
        super.onResume()

        Log.d("test@", selectedBottomMenu.toString())
        when (selectedBottomMenu) {
            0 -> homeFrame.getPostData()
            1 -> {

            }
            2 -> {
                likeFrame.getLikePostsData()
            }
            3 -> {
                moreFrame.getMyPostsData()
                moreFrame.getUserData()
            }
        }
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        title = resources.getString(R.string.app_name)
        toolbar_title.text = toolbar.title
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun initBottomNavigationView() {
        disableShiftMode(bnv_home)

        bnv_home.setOnNavigationItemSelectedListener { item ->

            layout_home.visibility = View.GONE
            layout_new.visibility = View.GONE
            layout_like.visibility = View.GONE
            layout_more.visibility = View.GONE

            when (item.itemId) {
                R.id.action_home -> {
                    selectedBottomMenu = 0
                    layout_home.visibility = View.VISIBLE
                    homeFrame.getPostData()
                }
                R.id.action_new -> {
                    selectedBottomMenu = 1
                    layout_new.visibility = View.VISIBLE
                }
                R.id.action_like -> {
                    selectedBottomMenu = 2
                    layout_like.visibility = View.VISIBLE
                    likeFrame.getLikePostsData()
                }
                R.id.action_more -> {
                    selectedBottomMenu = 3
                    layout_more.visibility = View.VISIBLE
                    moreFrame.getMyPostsData()
                    moreFrame.getUserData()
                }
            }
            true
        }
    }

    override fun onBackPressed() {
        var customTitle = TextView(this).apply {
            text = resources.getString(R.string.app_name)
            setPadding(32, 32, 32, 32)
            textSize = 16f
            setBackgroundColor(resources.getColor(R.color.colorPrimary))
            setTextColor(Color.WHITE)
        }

        AlertDialog.Builder(this)
                .setCustomTitle(customTitle)
                .setMessage("종료하시겠습니까?")
                .setPositiveButton("확인") { p0, p1 ->
                    finish()
                }
                .setNegativeButton("취소") { p0, p1 ->
                }.show()
//        super.onBackPressed()
    }
}
