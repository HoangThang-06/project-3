package com.example.project_3.data.model

data class User(
    val id_user: Int = 0,
    val username: String,
    val fullname: String? = null,
    val phone: String? = null,
    val birthday: String? = null,
    val gender: String? = null,
    val address: String? = null,
    val avatar: String? = null,
    val password: String,
    val role: String,
    val email: String,
    val created_at: String? = null,
    val status: String,

    val post_count: Int = 0,
    val adopt_count: Int = 0
)
