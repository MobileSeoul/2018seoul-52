package com.project.androidtest.app.post.update

import android.app.Activity
import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.project.androidtest.R
import com.project.androidtest.app.base.BaseActivity
import com.project.androidtest.app.post.detail.adapter.CommentAdapter
import com.project.androidtest.databinding.PostUpdateMainBinding
import com.project.androidtest.model.kakao.PlaceModel
import com.project.androidtest.model.post.PostModel
import com.project.androidtest.rest.ServerClient
import com.project.androidtest.rest.post.PostService
import com.project.androidtest.util.ACTION_GOTO_SEARCH_LOCATION
import com.project.androidtest.util.INTENT_KEY_ITEM
import com.project.androidtest.util.getRealPathFromURI
import com.project.androidtest.util.takePictureFromGallery
import com.project.androidtest.vm.CommentViewModel
import com.theartofdev.edmodo.cropper.CropImage
import com.uber.autodispose.AutoDispose
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.post_update_main.*
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import java.util.function.Consumer

class PostUpdateActivity : BaseActivity() {
    private lateinit var binding: PostUpdateMainBinding
    private lateinit var placeItem: PlaceModel
    private var fileUri: Uri? = null
    private lateinit var postItem: PostModel
    private var viewModel: CommentViewModel? = null
    private var commentAdapter: CommentAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.post_update_main)
        initToolbar()
        postItem = intent.getSerializableExtra(INTENT_KEY_ITEM) as PostModel
        binding.post = postItem
        placeItem = PlaceModel(postItem.location, "", postItem.longitude, postItem.latitude)

        viewModel = ViewModelProviders.of(this as FragmentActivity).get(CommentViewModel::class.java).apply {
            getComments().observe(this@PostUpdateActivity, Observer {
                if (it != null) {
                    commentAdapter?.setItems(it)
                }
            })
        }

        getCommentData(postItem.id)

        initCommentRecyclerView()

        btn_post_update_comment.setOnClickListener {
            var comment = et_post_update_comment.text.toString()
            viewModel?.createCommentData(this@PostUpdateActivity, lifecycle, Consumer {  }, comment, postItem.id)
            et_post_update_comment.setText("")
            Toast.makeText(this, "등록되었습니다.", Toast.LENGTH_SHORT).show()
        }

        btn_toolbar_delete.setOnClickListener {

            var customTitle = TextView(this).apply {
                text = "주의"
                setPadding(32, 32, 32, 32)
                textSize = 16f
                setBackgroundColor(resources.getColor(R.color.colorPrimary))
                setTextColor(Color.WHITE)
            }

            AlertDialog.Builder(this)
                    .setCustomTitle(customTitle)
                    .setMessage("정말 삭제하시겠습니까?")
                    .setPositiveButton("네") { p0, p1 ->

                        ServerClient.getOrCreate(PostService::class.java)
                                .deletePost(postItem.id)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe {
                                    Toast.makeText(this@PostUpdateActivity, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                    }
                    .setNegativeButton("아니오") { p0, p1 ->

                    }.show()
        }

        btn_post_update_category.setOnClickListener {
            val categoryList = arrayOf("책임분양", "찾습니다", "찾았습니다")

            var customTitle = TextView(this).apply {
                text = "카테고리 선택"
                setPadding(32, 32, 32, 32)
                textSize = 16f
                setBackgroundColor(resources.getColor(R.color.colorPrimary))
                setTextColor(Color.WHITE)
            }


            AlertDialog.Builder(this)
                    .setCustomTitle(customTitle)
                    .setItems(categoryList) { p0, p1 ->
                        when (p1) {
                            0 -> {
                                btn_post_update_category.text = categoryList[p1]
                            }
                            1 -> {
                                btn_post_update_category.text = categoryList[p1]
                            }
                            2 -> {
                                btn_post_update_category.text = categoryList[p1]
                            }
                        }
                    }.show()
        }

        btn_post_update_location.setOnClickListener {
            startActivityForResult(Intent(ACTION_GOTO_SEARCH_LOCATION), 2000)
        }

        btn_toolbar_save.setOnClickListener {
            updatePost(
                    et_post_update_title.text.toString(),
                    placeItem.roadAddressName,
                    et_post_update_description.text.toString(),
                    btn_post_update_category.text.toString(),
                    placeItem.y,
                    placeItem.x
            )
        }

        iv_post_update_poster.setOnClickListener {
            takePictureFromGallery()
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                var position = tab?.position
                layout_post_update_01.visibility = View.GONE
                layout_post_update_02.visibility = View.GONE

                when (position) {
                    0 -> layout_post_update_01.visibility = View.VISIBLE
                    1 -> layout_post_update_02.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun initCommentRecyclerView() {
        commentAdapter = CommentAdapter()
        rv_post_update_comment.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
            adapter = commentAdapter
        }
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        title = "포스트 업데이트"
        toolbar_title.text = toolbar.title
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_arrow_left)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (data != null) {
                var result: CropImage.ActivityResult = CropImage.getActivityResult(data)
                if (resultCode == RESULT_OK) {
                    var resultUri = result.uri
                    fileUri = resultUri
                    try {
                        MediaStore.Images.Media.getBitmap(contentResolver, resultUri)?.let {
                            iv_post_update_poster.setImageBitmap(it)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    var error = result.error
                }
            } else {
                Toast.makeText(this, "불러온 사진이 없습니다.", Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == 2000) {
            if (resultCode == Activity.RESULT_OK) {
                placeItem = data?.getSerializableExtra(INTENT_KEY_ITEM) as PlaceModel
                btn_post_update_location.text = placeItem.roadAddressName
            }
        }
    }

    private fun updatePost(title: String, location: String, description: String, category: String, latitude: String, longitude: String) {
        var partMap: Map<String, RequestBody>

        if (fileUri != null) {
            val file = File(getRealPathFromURI(fileUri!!))
            val requestFile = RequestBody.create(MediaType.parse("image/*"), file)

            var title = RequestBody.create(MediaType.parse("text/plain"), title)
            var location = RequestBody.create(MediaType.parse("text/plain"), location)
            var description = RequestBody.create(MediaType.parse("text/plain"), description)
            var category = RequestBody.create(MediaType.parse("text/plain"), category)
            var latitude = RequestBody.create(MediaType.parse("text/plain"), latitude)
            var longitude = RequestBody.create(MediaType.parse("text/plain"), longitude)

            partMap = mutableMapOf<String, RequestBody>(
                    "poster\"; filename=\"${file.name}\"" to requestFile,
                    "title" to title,
                    "location" to location,
                    "description" to description,
                    "category" to category,
                    "latitude" to latitude,
                    "longitude" to longitude
            )

            ServerClient.getOrCreate(PostService::class.java)
                    .updatePost(postItem.id, partMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .`as`(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(lifecycle)))
                    .subscribe({
                        Toast.makeText(this@PostUpdateActivity, "수정되었습니다.", Toast.LENGTH_SHORT).show()
                        finish()
                    }, {
                    })
        } else {
            var title = RequestBody.create(MediaType.parse("text/plain"), title)
            var location = RequestBody.create(MediaType.parse("text/plain"), location)
            var description = RequestBody.create(MediaType.parse("text/plain"), description)
            var category = RequestBody.create(MediaType.parse("text/plain"), category)
            var latitude = RequestBody.create(MediaType.parse("text/plain"), latitude)
            var longitude = RequestBody.create(MediaType.parse("text/plain"), longitude)

            partMap = mutableMapOf<String, RequestBody>(
                    "title" to title,
                    "location" to location,
                    "description" to description,
                    "category" to category,
                    "latitude" to latitude,
                    "longitude" to longitude
            )

            ServerClient.getOrCreate(PostService::class.java)
                    .updatePost(postItem.id, partMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .`as`(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(lifecycle)))
                    .subscribe({
                        Toast.makeText(this@PostUpdateActivity, "수정되었습니다.", Toast.LENGTH_SHORT).show()
                        finish()
                    }, {
                    })
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
