package com.example.project_3.data.model

import com.google.gson.annotations.SerializedName

data class Article(
    // 1. CÁC TRƯỜNG KHỚP 100% VỚI DATABASE THỰC TẾ
    val id_article: Int,
    val title: String = "",       // Cột title (Varchar) mới bổ sung từ Database
    val content: String,
    val image: String,
    val category: String,         // Chuỗi chứa tag ví dụ: #meomayman
    val click: Int = 0,           // Cột click (Int) mới bổ sung từ Database
    val status: String = "public", // Cột status (Enum) mới bổ sung từ Database ('public' / 'private')

    // 2. CÁC TRƯỜNG DÙNG CHO PHÍA USER (Giao diện SocialScreen)
    // Đặt giá trị mặc định để nếu API phía Admin không trả về, App vẫn không bị crash hoặc lỗi biên dịch
    val author_name: String = "Admin",
    val author_avatar: String = "",

    @SerializedName("author_address")
    val authorAddress: String = "Việt Nam",

    val likes_count: Int = 0,
    val comments_count: Int = 0,
    val create_at: String = "",

    // Trạng thái kiểm tra xem User hiện tại đã bấm thích bài viết chưa (0: Chưa thích, 1: Đã thích)
    @SerializedName("is_liked")
    val isLiked: Int = 0
)