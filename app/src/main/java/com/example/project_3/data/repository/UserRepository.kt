package com.example.project_3.data.repository

import com.example.project_3.data.remote.RetrofitClient

class UserRepository {

    suspend fun login(
        username: String,
        password: String
    ) =
        RetrofitClient.api.login(
            username,
            password
        )

    suspend fun register(
        username: String,
        password: String,
        email: String
    ) =
        RetrofitClient.api.register(
            username,
            password,
            email
        )
}