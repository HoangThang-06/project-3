package com.example.project_3.data.repository

import com.example.project_3.data.model.UserResponse
import com.example.project_3.data.remote.RetrofitClient

class UserRepository {

    // =========================
    // AUTH
    // =========================

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

    // =========================
    // USER
    // =========================

    // Thay vì dùng apiService.updateProfile, hãy chuyển sang dùng cấu trúc RetrofitClient của dự án
    suspend fun updateAdminProfile(
        idUser: Int,
        fullname: String,
        phone: String,
        birthday: String,
        gender: String,
        address: String,
        email: String
    ): UserResponse {
        return RetrofitClient.api.updateProfile(idUser, fullname, phone, birthday, gender, address, email)
    }
    suspend fun getUser(
        idUser: String
    ): UserResponse {

        return RetrofitClient.api.getUser(idUser)
    }

    suspend fun getAllUsers(
        currentUserId: String
    ): UserResponse {

        return RetrofitClient.api.getAllUsers(currentUserId)
    }

    suspend fun deleteUser(
        idUser: String
    ): UserResponse {
        return RetrofitClient.api.deleteUser(idUser)
    }

    suspend fun resetPassword(
        email: String,
        newPassword: String
    ): UserResponse {

        return RetrofitClient.api.resetPassword(
            email,
            newPassword
        )
    }
}