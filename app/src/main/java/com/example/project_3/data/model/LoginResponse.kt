package com.example.project_3.data.model

data class LoginResponse(

    val success: Boolean,

    val message: String,

    val user: User? = null
)