package com.project.androidtest.model.user

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UserModel(
        var id: Int,
        var phone: String,
        var poster: String,
        var nickname: String,
        var user: String,
        @SerializedName("created_at")
        var createdAt: String,
        @SerializedName("updated_at")
        var updatedAt: String
) : Serializable