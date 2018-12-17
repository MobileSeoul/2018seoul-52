package com.project.androidtest.app.user.my

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.FragmentActivity
import android.view.MenuItem
import android.widget.Toast
import com.bumptech.glide.Glide
import com.project.androidtest.R
import com.project.androidtest.app.base.BaseActivity
import com.project.androidtest.databinding.MyProfileMainBinding
import com.project.androidtest.model.user.UserModel
import com.project.androidtest.rest.ServerClient
import com.project.androidtest.rest.user.UserService
import com.project.androidtest.util.PREF_KEY_USER_ID
import com.project.androidtest.util.PreferenceHelper
import com.project.androidtest.util.getRealPathFromURI
import com.project.androidtest.util.takePictureFromGallery
import com.project.androidtest.vm.UserViewModel
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.uber.autodispose.AutoDispose
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.my_profile_main.*
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import java.util.function.Consumer

class MyProfileActivity : BaseActivity() {
    private lateinit var binding: MyProfileMainBinding
    private lateinit var currentUser: UserModel
    private lateinit var updateUser: UserModel
    private var fileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.my_profile_main)

        var viewModel = ViewModelProviders.of(this as FragmentActivity).get(UserViewModel::class.java)
        viewModel.getUser().observe(this, Observer {
            initToolbar("${it?.user}의 프로필")
            binding.user = it
            currentUser = it!!
            Glide.with(this)
                    .load(it.poster)
                    .into(iv_my_profile_poster)
        })
        viewModel.getUserData(lifecycle, Consumer {  }, PreferenceHelper.getInstance(this@MyProfileActivity).getIntPreference(PREF_KEY_USER_ID))

        btn_toolbar_save.setOnClickListener {
            currentUser.apply {
                nickname = et_my_profile_nickname.text.toString()
                phone = et_my_profile_phone.text.toString()
            }


            var password = RequestBody.create(MediaType.parse("text/plain"), et_my_profile_password.text.toString())
            var nickname = RequestBody.create(MediaType.parse("text/plain"), currentUser.nickname)
            var phone = RequestBody.create(MediaType.parse("text/plain"), currentUser.phone)

            var partMap: MutableMap<String, RequestBody>

            if (fileUri != null) {
                val file = File(getRealPathFromURI(fileUri!!))
                val requestFile = RequestBody.create(MediaType.parse("image/*"), file)

                partMap = if (et_my_profile_password.text.toString() == "") {
                    mutableMapOf<String, RequestBody>(
                            "poster\"; filename=\"${file.name}\"" to requestFile,
                            "nickname" to nickname,
                            "phone" to phone
                    )
                } else {
                    mutableMapOf<String, RequestBody>(
                            "poster\"; filename=\"${file.name}\"" to requestFile,
                            "password" to password,
                            "nickname" to nickname,
                            "phone" to phone
                    )
                }
            } else {
                partMap = if (et_my_profile_password.text.toString() == "") {
                    mutableMapOf<String, RequestBody>(
                            "nickname" to nickname,
                            "phone" to phone
                    )
                } else {
                    mutableMapOf<String, RequestBody>(
                            "password" to password,
                            "nickname" to nickname,
                            "phone" to phone
                    )
                }
            }

            ServerClient.getOrCreate(UserService::class.java)
                    .updateUser(PreferenceHelper.getInstance(this).getIntPreference(PREF_KEY_USER_ID), partMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .`as`(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(lifecycle)))
                    .subscribe({
                        Toast.makeText(this@MyProfileActivity, "저장되었습니다.", Toast.LENGTH_SHORT).show()
                        finish()
                    }, {
                    })
        }

        iv_my_profile_poster.setOnClickListener {
            takePictureFromGallery()
        }
    }

    private fun initToolbar(titleStr: String?) {
        setSupportActionBar(toolbar)
        title = titleStr
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
                            Glide.with(this)
                                    .load(it)
                                    .into(iv_my_profile_poster)
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
        }
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
