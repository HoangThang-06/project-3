package com.example.project_3.data.model

// Khối phản hồi danh sách đơn nhận nuôi từ get_all_requests.php
data class AdoptionResponse(
    val success: Boolean,
    val message: String,
    val data: List<AdoptionDetail>? = null
)

// Khối gửi thông tin yêu cầu nhận nuôi lên server (Dùng cho POST request)

// Đảm bảo class này nằm RIÊNG BIỆT, không bị lồng bên trong 2 class trên
data class AdoptionDetail(
    val id: Int,
    val user_name: String,
    val name_pet: String,
    val species: String,
    val state: String,
    val age: Int,
    val email: String,
    val adoption_date: String
)