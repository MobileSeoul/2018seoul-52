package com.project.androidtest.app.post.create

import android.annotation.SuppressLint
import android.app.Activity
import android.databinding.DataBindingUtil
import android.databinding.ObservableField
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.project.androidtest.R
import android.provider.MediaStore
import android.content.Intent
import android.graphics.Color
import android.widget.Toast
import android.net.Uri
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.TextView
import com.project.androidtest.databinding.PostCreateMainBinding
import com.project.androidtest.model.kakao.PlaceModel
import com.project.androidtest.rest.ServerClient
import com.project.androidtest.rest.post.PostService
import com.project.androidtest.util.*
import com.theartofdev.edmodo.cropper.CropImage
import com.uber.autodispose.AutoDispose
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.post_create_main.*
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.File
import java.io.IOException

class PostCreateActivity : AppCompatActivity() {
    private var TAG = PostCreateActivity::class.java.simpleName
    private lateinit var binding: PostCreateMainBinding
    private var categoryType: ObservableField<String> = ObservableField("책임분양")
    private var fileUri: Uri? = null
    private lateinit var placeItem: PlaceModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.post_create_main)

        initToolbar()

        initClickListener()

        btn_toolbar_save.setOnClickListener {
            var title = et_post_create_title.text.toString()
            var location = btn_post_create_location.text.toString()
            var description = et_post_create_description.text.toString()
            var category = categoryType.get()

            if (title == "" || location == "" || description == "" || category == "" || fileUri == null) {
                Toast.makeText(this@PostCreateActivity, "모든항목을 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                category?.let { it1 -> createPost(title, location, description, it1, placeItem.y, placeItem.x) }
            }
        }

        btn_post_create_location.setOnClickListener {
            startActivityForResult(Intent(ACTION_GOTO_SEARCH_LOCATION), 2000)
        }
    }

    private fun initClickListener() {
        iv_post_create_poster.setOnClickListener {
            takePictureFromGallery()
        }

        btn_post_create_category.setOnClickListener {
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
                                categoryType.set("책임분양")
                                btn_post_create_category.text = "책임분양"
                            }
                            1 -> {
                                categoryType.set("찾습니다")
                                btn_post_create_category.text = "찾습니다"
                            }
                            2 -> {
                                categoryType.set("찾았습니다")
                                btn_post_create_category.text = "찾았습니다"
                            }
                        }
                    }.show()
        }
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        title = "등록하기"
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
                            view_post_create_poster_empty.visibility = View.GONE
                            iv_post_create_poster.setImageBitmap(it)
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
                btn_post_create_location.text = placeItem.roadAddressName
            }
        }
    }

    private fun createPost(title: String, location: String, description: String, category: String, latitude: String, longitude: String) {
        var partMap: Map<String, RequestBody>

        if (fileUri != null) {
            val file = File(getRealPathFromURI(fileUri!!))
            val requestFile = RequestBody.create(MediaType.parse("image/*"), file)

            var user = RequestBody.create(MediaType.parse("text/plain"), PreferenceHelper.getInstance(this@PostCreateActivity).getIntPreference(PREF_KEY_USER_ID).toString())
            var title = RequestBody.create(MediaType.parse("text/plain"), title)
            var location = RequestBody.create(MediaType.parse("text/plain"), location)
            var description = RequestBody.create(MediaType.parse("text/plain"), description)
            var category = RequestBody.create(MediaType.parse("text/plain"), category)
            var latitude = RequestBody.create(MediaType.parse("text/plain"), latitude)
            var longitude = RequestBody.create(MediaType.parse("text/plain"), longitude)

            partMap = mutableMapOf<String, RequestBody>(
                    "poster\"; filename=\"${file.name}\"" to requestFile,
                    "user" to user,
                    "title" to title,
                    "location" to location,
                    "description" to description,
                    "category" to category,
                    "latitude" to latitude,
                    "longitude" to longitude
            )

            ServerClient.getOrCreate(PostService::class.java)
                    .createPost(partMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .`as`(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(lifecycle)))
                    .subscribe({
                        Toast.makeText(this@PostCreateActivity, "등록되었습니다.", Toast.LENGTH_SHORT).show()
                        finish()
                    }, {
                    })
        } else {

        }

    }
}
