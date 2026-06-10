package com.example.project_3.data.model

data class AddCommentResponse(
    val success: Boolean,
    val message: String,
    val comment: Comment
)