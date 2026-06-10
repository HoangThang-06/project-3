package com.example.project_3.data.model

import com.google.gson.annotations.SerializedName

data class Pet(

    val id_pet: Int,

    val name_pet: String,

    val gender: String,

    val description: String,

    val state: String,

    val image: String,

    val click: Int,

    val age: Int,

    val species: String,

    // BẮT BUỘC PHẢI THÊM DÒNG NÀY ĐỂ HỨNG ĐÚNG TÊN TỪ PHP TRẢ VỀ
    @SerializedName("is_followed")
    var isFollowed: Int = 0
)