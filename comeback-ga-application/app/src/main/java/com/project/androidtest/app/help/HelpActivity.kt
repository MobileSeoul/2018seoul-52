package com.project.androidtest.app.help

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.project.androidtest.R
import kotlinx.android.synthetic.main.help_main.*

class HelpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.help_main)

        initToolbar()

        Glide.with(this)
                .load(R.drawable.img_help)
                .into(iv_help_poster)
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        title = "유기동물 발견시 대처방법"
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
}
