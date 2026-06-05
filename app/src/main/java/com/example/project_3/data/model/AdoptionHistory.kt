package com.example.project_3.data.model

import com.google.gson.annotations.SerializedName

data class AdoptionHistory(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("breed")
    val breed: String,

    @SerializedName("age")
    val age: String,

    // Nhận trực tiếp chuỗi 'pending', 'approved', 'rejected' từ ENUM của DB
    @SerializedName("status")
    val status: String,

    @SerializedName("dateSubmitted")
    val dateSubmitted: String,

    @SerializedName("imageUrl")
    val imageUrl: String
)