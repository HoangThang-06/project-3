package com.example.project_3.data.model

data class CommentResponse(
    val success: Boolean,
    val data: List<Comment>,
    val has_more: Boolean
)