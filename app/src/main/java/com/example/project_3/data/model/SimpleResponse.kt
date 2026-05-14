package com.example.project_3.data.model

data class SimpleResponse(
    val success: Boolean,
    val message: String? = null,
    val action: String? = null // Trả về "liked" hoặc "unliked" từ PHP
)
