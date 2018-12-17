package com.project.androidtest.rest.user

import com.project.androidtest.model.like.LikeModel
import com.project.androidtest.model.post.PostModel
import com.project.androidtest.model.user.UserModel
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface UserService {
    @Multipart
    @POST("api/user")
    fun createUser(
            @PartMap partMap: Map<String, @JvmSuppressWildcards RequestBody>
    ) : Observable<UserModel>

    @Multipart
    @PUT("api/user/{id}")
    fun updateUser(
            @Path("id") id: Int,
            @PartMap partMap: Map<String, @JvmSuppressWildcards RequestBody>
    ) : Observable<Any>

    @FormUrlEncoded
    @POST("api/user/auth")
    fun authUser(
            @Field("user") user: String,
            @Field("password") password: String
    ) : Observable<UserModel>

    @GET("api/user/{id}/liked-posts")
    fun getLikedPosts(
            @Path("id") userId: Int
    ) : Observable<MutableList<LikeModel>>

    @GET("api/user/{id}")
    fun getUserProfile(
            @Path("id") userId: Int
    ) : Observable<UserModel>

    @GET("api/user/{id}/posts")
    fun getUserPosts(
        @Path("id") userId: Int
    ) : Observable<MutableList<PostModel>>


    @GET("api/user/{user_id}/post/{post_id}/comment-like")
    fun createCommentLike(
            @Path("user_id") userId: Int,
            @Path("post_id") postId: Int
    ) : Observable<Any>

}