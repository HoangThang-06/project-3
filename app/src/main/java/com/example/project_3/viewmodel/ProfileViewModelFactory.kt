package com.example.project_3.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.project_3.data.local.SessionManager
import com.example.project_3.viewmodel.EditProfileViewModel // THÊM IMPORT NÀY
import com.example.project_3.viewmodel.ProfileViewModel

class ProfileViewModelFactory(private val sessionManager: SessionManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // 1. Kiểm tra nếu màn hình yêu cầu ProfileViewModel (Màn hình Profile chính)
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(sessionManager) as T
        }

        // 2. THÊM ĐOẠN NÀY: Kiểm tra nếu màn hình yêu cầu EditProfileViewModel (Màn hình chỉnh sửa)
        if (modelClass.isAssignableFrom(EditProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditProfileViewModel(sessionManager) as T
        }

        // Nếu truyền vào một class ViewModel lạ lẫm khác ngoài 2 cái trên mới báo lỗi
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}