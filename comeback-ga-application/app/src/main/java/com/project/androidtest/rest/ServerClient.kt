package com.project.androidtest.rest

import com.project.androidtest.util.SERVER_URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import okhttp3.logging.HttpLoggingInterceptor



class ServerClient {
    companion object {
        private var instance: ServerClient? = null

        private fun getInstance(): ServerClient {
            if (instance == null) instance = ServerClient()
            return instance as ServerClient
        }

        fun <T> getOrCreate(tClass: Class<T>): T {
            return getInstance().getOrCreateInternal(tClass)
        }
    }

    private var retrofit: Retrofit? = null

    init {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        interceptor.level = HttpLoggingInterceptor.Level.HEADERS

        var okHttpClient: OkHttpClient = OkHttpClient.Builder()
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(100, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build()

        retrofit = Retrofit.Builder()
                .baseUrl(SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build()
    }

    private val serviceHashMap = mutableMapOf<Any, Any>()

    private fun <T> getOrCreateInternal(tClass: Class<T>): T {
        if (serviceHashMap.containsKey(tClass)) {
            return serviceHashMap[tClass] as T
        }
        var service: T = retrofit?.create(tClass) as T
        serviceHashMap.plus(tClass to service)
        return service
    }
}