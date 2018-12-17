package com.project.androidtest.model.like

import com.google.gson.annotations.SerializedName
import com.project.androidtest.model.post.PostModel

data class LikeModel(
        var post: PostModel,
        @SerializedName("created_at")
        var createdAt: String,
        @SerializedName("updated_at")
        var updatedAt: String
)