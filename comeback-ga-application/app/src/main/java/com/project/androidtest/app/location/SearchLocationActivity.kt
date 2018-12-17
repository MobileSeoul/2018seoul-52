package com.project.androidtest.app.location

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.AdapterView
import com.project.androidtest.R
import com.project.androidtest.app.location.adapter.SearchLocationAdapter
import com.project.androidtest.model.kakao.PlaceModel
import com.project.androidtest.util.INTENT_KEY_ITEM
import com.project.androidtest.vm.KakaoViewModel
import kotlinx.android.synthetic.main.search_location_main.*
import android.view.KeyEvent.KEYCODE_ENTER
import android.widget.Toast


class SearchLocationActivity : AppCompatActivity() {
    private val TAG = SearchLocationActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_location_main)

        view_search_location_empty.text = "검색어를 입력해주세요."

        var viewModel = ViewModelProviders.of(this as FragmentActivity).get(KakaoViewModel::class.java)

        var searchLocationAdapter = SearchLocationAdapter()

        viewModel.getPlaces().observe(this, Observer {
            Log.d(TAG, it.toString())
            view_search_location_empty.text = "검색된 결과가 없습니다."
            it?.let { it1 -> searchLocationAdapter.setItems(it1) }
            if (it?.size == 0) {
                view_search_location_empty.visibility = View.VISIBLE
            } else {
                view_search_location_empty.visibility = View.GONE
            }
        })

        rv_search_location.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
            adapter = searchLocationAdapter
        }

        searchLocationAdapter.setOnItemClickListener(object: SearchLocationAdapter.OnItemClickListener{
            override fun onItemClick(item: PlaceModel) {
                Intent().apply {
                    putExtra(INTENT_KEY_ITEM, item)
                    setResult(Activity.RESULT_OK, this)
                    finish()
                }
            }
        })

        btn_search_location_search.setOnClickListener {
            var query = et_search_location_search.text.toString()
            viewModel.getPlacesData(query)
        }

        et_search_location_search.setOnKeyListener { p0, p1, event ->
            when (event.action) {
                KeyEvent.ACTION_DOWN and KeyEvent.KEYCODE_ENTER-> {
                    var query = et_search_location_search.text.toString()
                    viewModel.getPlacesData(query)
                    true
                }
            }
            false
        }
    }
}
