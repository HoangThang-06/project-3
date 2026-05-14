package com.example.project_3.data.model

data class Article(
    val id_article: Int,
    val author_name: String,   // Tên người đăng bài
    val author_avatar: String, // Ảnh đại diện người đăng bài
    val content: String,
    val image: String,         // Ảnh bài viết
    val category: String,      // Hashtag
    val likes_count: Int,
    val comments_count: Int,
    val create_at: String
)
