package com.example.project_3.data.model

import com.google.gson.annotations.SerializedName

data class AdminActivity(
    @SerializedName("boldText")
    val boldText: String,

    @SerializedName("normalText")
    val normalText: String,

    @SerializedName("timeText")
    val timeText: String,

    @SerializedName("type")
    val type: String // "adopt" hoặc "rescue"
)