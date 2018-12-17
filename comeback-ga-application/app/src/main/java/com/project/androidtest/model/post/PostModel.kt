package com.project.androidtest.model.post

import com.google.gson.annotations.SerializedName
import com.project.androidtest.model.user.UserModel
import java.io.Serializable

data class PostModel (
        var id: Int,
        var user: UserModel,
        var title: String,
        var description: String,
        var location: String,
        var poster: String,
        var category: String,
        var status: Boolean,
        var latitude: String,
        var longitude: String,
        @SerializedName("created_at")
        var createdAt: String,
        @SerializedName("updated_at")
        var updatedAt: String
) : Serializable