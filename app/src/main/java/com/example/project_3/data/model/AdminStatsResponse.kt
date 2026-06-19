package com.example.project_3.data.model

import com.google.gson.annotations.SerializedName

data class AdminStatsResponse(
    @SerializedName("success")
    val success: Boolean,

    // Bắt buộc phải có @SerializedName trùng khớp chính xác từng ký tự với chuỗi JSON từ PHP trả về
    @SerializedName("total_pets")
    val total_pets: Int,

    @SerializedName("total_adoptions")
    val total_adoptions: Int,

    @SerializedName("active_rescues")
    val active_rescues: Int,

    @SerializedName("recent_activities")
    val recent_activities: List<AdminActivity>
)