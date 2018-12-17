package com.project.androidtest.rest.post

import com.project.androidtest.model.comment.CommentModel
import com.project.androidtest.model.post.PostModel
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.*

interface PostService {
    @GET("api/post")
    fun getPostList() : Observable<MutableList<PostModel>>

    @GET("api/post/{id}/comment")
    fun getCommentList(
            @Path("id") postId: Int
    ) : Observable<MutableList<CommentModel>>

    @FormUrlEncoded
    @POST("api/post/{id}/comment")
    fun createComment(
        @Path("id") postId: Int,
        @Field("user") user: Int,
        @Field("comment") comment: String
    ) : Observable<Any>

    @Multipart
    @POST("api/post")
    fun createPost(
            @PartMap partMap: Map<String, @JvmSuppressWildcards RequestBody>
    ) : Observable<Any>

    @Multipart
    @PUT("api/post/{id}")
    fun updatePost(
            @Path("id") id: Int,
            @PartMap partMap: Map<String, @JvmSuppressWildcards RequestBody>
    ) : Observable<Any>

    @DELETE("api/post/{id}")
    fun deletePost(
            @Path("id") id: Int
    ) : Observable<Any>
}