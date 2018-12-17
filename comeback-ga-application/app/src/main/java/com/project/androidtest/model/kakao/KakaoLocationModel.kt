package com.project.androidtest.model.kakao

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class KakaoLocationModel(
        var documents: MutableList<PlaceModel>
)
//        "address_name": "충남 천안시 동남구 대흥동 57-1",
//        "category_group_code": "",
//        "category_group_name": "",
//        "category_name": "교통,수송 > 기차,철도 > 기차역",
//        "distance": "",
//        "id": "23150933",
//        "phone": "1544-7788",
//        "place_name": "천안역",
//        "place_url": "http://place.map.daum.net/23150933",
//        "road_address_name": "충남 천안시 동남구 대흥로 239",
//        "x": "127.14684641355433",
//        "y": "36.80900298061322"
data class PlaceModel(
        @SerializedName("road_address_name")
        var roadAddressName: String,
        @SerializedName("place_name")
        var placeName: String,
        var x: String,
        var y: String
) : Serializable
