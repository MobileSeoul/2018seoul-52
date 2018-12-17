package com.project.androidtest.app.home.new

import android.arch.lifecycle.Lifecycle
import android.content.Intent
import android.view.View
import com.project.androidtest.app.base.BaseFrame
import com.project.androidtest.vm.PostViewModel
import com.project.androidtest.util.ACTION_GOTO_POST_CREATE
import kotlinx.android.synthetic.main.new_content.view.*

class NewFrame(contentView: View, parentLifecycle: Lifecycle): BaseFrame(contentView, parentLifecycle) {
    private var viewModel: PostViewModel? = null

    init {
        contentView.btn_new_content_register.setOnClickListener {
            startActivity(Intent(ACTION_GOTO_POST_CREATE))
        }
    }
}