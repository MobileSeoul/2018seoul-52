package com.project.androidtest.model.comment

import com.google.gson.annotations.SerializedName
import com.project.androidtest.model.post.PostModel
import com.project.androidtest.model.user.UserModel

data class CommentModel(
        var id: Int,
        var post: PostModel,
        var user: UserModel,
        var comment: String,
        @SerializedName("created_at")
        var createdAt: String,
        @SerializedName("updated_at")
        var updatedAt: String
)