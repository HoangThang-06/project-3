package com.example.project_3.data.remote

import com.example.project_3.data.model.AdoptionHistory
import com.google.gson.annotations.SerializedName

data class AdoptHistoryResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String? = null,

    // Mảng danh sách chứa các hồ sơ lịch sử
    @SerializedName("history")
    val history: List<AdoptionHistory>? = null
)