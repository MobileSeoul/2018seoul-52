package com.project.androidtest.rest.kakao

import com.project.androidtest.model.kakao.KakaoLocationModel
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface KakaoService {
    @GET("v2/local/search/keyword.json")
    fun getPlaces(
            @Header("Authorization") authorization: String,
            @Query("query") query: String
    ) : Observable<KakaoLocationModel>
}