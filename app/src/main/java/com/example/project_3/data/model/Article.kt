package com.example.project_3.data.model

import com.google.gson.annotations.SerializedName

data class Article(
    val id_article: Int,
    val author_name: String,
    val author_avatar: String,

    @SerializedName("author_address")
    val authorAddress: String = "Việt Nam", // Hứng địa chỉ động

    val content: String,
    val image: String,
    val category: String, // Chuỗi chứa: #meomayman #hanhphuc
    val likes_count: Int,
    val comments_count: Int,
    val create_at: String,

    // Thêm trường này để nhận biết User hiện tại đã bấm Tim bài viết này chưa
    @SerializedName("is_liked")
    val isLiked: Int = 0
)