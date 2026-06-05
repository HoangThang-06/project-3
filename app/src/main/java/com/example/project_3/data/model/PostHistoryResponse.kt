package com.example.project_3.data.model

data class PostHistoryResponse(
    val success: Boolean,
    val message: String?,
    val posts: List<UserPost>?
)

data class UserPost(
    val id: Int,
    val content: String,
    val imageUrl: String,
    val category: String,
    val likes: Int,
    val comments: Int,
    val date: String,
    val status: String
)
