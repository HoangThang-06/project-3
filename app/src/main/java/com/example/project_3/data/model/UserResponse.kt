package com.example.project_3.data.model

data class UserResponse(

    val success: Boolean,

    val message: String? = null,

    val user: User? = null,

    val users: List<User>? = null
)
