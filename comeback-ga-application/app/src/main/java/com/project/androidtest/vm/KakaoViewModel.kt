package com.project.androidtest.vm

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.project.androidtest.model.kakao.PlaceModel
import com.project.androidtest.rest.KakaoServerClient
import com.project.androidtest.rest.kakao.KakaoService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class KakaoViewModel : ViewModel() {
    private var places = MutableLiveData<MutableList<PlaceModel>>()


    fun getPlaces(): LiveData<MutableList<PlaceModel>> = places

    fun getPlacesData(query: String) {
        KakaoServerClient.getOrCreate(KakaoService::class.java)
                .getPlaces("KakaoAK fabd4553d757928a8fd543adfbfd107f", query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    places.postValue(it.documents)
                }, {

                })
    }
}