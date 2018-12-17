package com.project.androidtest.util

import android.Manifest
import android.app.Activity
import android.net.Uri
import android.provider.MediaStore
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.util.ArrayList

fun Activity.getRealPathFromURI(contentURI: Uri): String {
    val result: String
    val cursor = this.contentResolver.query(contentURI, null, null, null, null)

    if (cursor == null) {
        result = contentURI.path
    } else {
        cursor.moveToFirst()
        val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        result = cursor.getString(idx)
        cursor.close()
    }

    return result
}

fun Activity.takePictureFromGallery() {
    CropImage.activity()
            .setGuidelines(CropImageView.Guidelines.ON)
            .start(this)
}

fun Activity.checkPermissions(permissions: Array<String>) {
    val permissionListener = object : PermissionListener {
        override fun onPermissionGranted() {
        }

        override fun onPermissionDenied(deniedPermissions: ArrayList<String>?) {

        }
    }

    TedPermission.with(this)
            .setPermissionListener(permissionListener)
            .setRationaleMessage("서비스를 사용하기 위해서는 권한허가가 필요합니다.")
            .setDeniedMessage("[설정] > [권한] 에서 권한을 허용할 수 있습니다.")
            .setPermissions(*permissions)
            .check()
}