package com.project.androidtest.app.auth.signup

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.Toast
import com.project.androidtest.R
import com.project.androidtest.rest.ServerClient
import com.project.androidtest.rest.user.UserService
import com.project.androidtest.util.getRealPathFromURI
import com.project.androidtest.util.takePictureFromGallery
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.uber.autodispose.AutoDispose
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.sign_up_main.*
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.File
import java.io.IOException

class SignUpActivity : AppCompatActivity() {
    private val TAG = SignUpActivity::class.java.simpleName
    private var fileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_up_main)

        initToolbar()

        iv_sign_up_poster.setOnClickListener {
            takePictureFromGallery()
        }

        btn_sign_up_save.setOnClickListener {
            var user = RequestBody.create(MediaType.parse("text/plain"), et_sign_up_user.text.toString())
            var password = RequestBody.create(MediaType.parse("text/plain"), et_sign_up_password.text.toString())
            var nickname = RequestBody.create(MediaType.parse("text/plain"), et_sign_up_nickname.text.toString())
            var phone = RequestBody.create(MediaType.parse("text/plain"), et_sign_up_phone.text.toString())

            var partMap: Map<String, RequestBody>


            if (fileUri != null) {
                val file = File(getRealPathFromURI(fileUri!!))
                val requestFile = RequestBody.create(MediaType.parse("image/*"), file)
                partMap = mutableMapOf<String, RequestBody>(
                        "poster\"; filename=\"${file.name}\"" to requestFile,
                        "user" to user,
                        "password" to password,
                        "nickname" to nickname,
                        "phone" to phone
                )
            } else {
                partMap = mutableMapOf<String, RequestBody>(
                        "user" to user,
                        "password" to password,
                        "nickname" to nickname,
                        "phone" to phone
                )
            }

            ServerClient.getOrCreate(UserService::class.java)
                    .createUser(partMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .`as`(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(lifecycle)))
                    .subscribe({
                        finish()
                        Toast.makeText(this@SignUpActivity, "회원가입 성공", Toast.LENGTH_SHORT).show()
                    }, {
                        Toast.makeText(this@SignUpActivity, "회원가입 실패", Toast.LENGTH_SHORT).show()
                    })
        }
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        title = "회원가입"
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
                            iv_sign_up_poster.setImageBitmap(it)
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
