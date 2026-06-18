package com.example.project_3.data.repository

import com.example.project_3.data.remote.RetrofitClient
import com.example.project_3.data.model.NotificationResponse
import com.example.project_3.data.model.HomeResponse

class HomeRepository {

    // ========================================================
    // 1. LẤY DANH SÁCH THÔNG BÁO TỪ SERVER (RETROFIT)
    // ========================================================
    suspend fun getNotificationsFromServer(userId: Int): NotificationResponse {
        // Gọi trực tiếp thông qua RetrofitClient toàn cục của dự án
        return RetrofitClient.api.getNotifications(userId)
    }

    // ========================================================
    // 2. LẤY DỮ LIỆU TỔNG HỢP TRANG CHỦ (SỰ KIỆN, PET, KIẾN THỨC)
    // ========================================================
    suspend fun getHomeDataFromServer(): HomeResponse {
        // Cầu nối giúp ViewModel lấy dữ liệu trang chủ từ API get_home_data.php
        return RetrofitClient.api.getHomeData()
    }
}