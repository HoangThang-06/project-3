package com.example.project_3.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.project_3.data.local.SessionManager
import com.example.project_3.viewmodel.EditProfileViewModel
import com.example.project_3.viewmodel.ProfileViewModel
import com.example.project_3.viewmodel.PostHistoryViewModel
import com.example.project_3.viewmodel.AdoptHistoryViewModel // Thêm import này khi bạn tạo ViewModel lịch sử

class ProfileViewModelFactory(private val sessionManager: SessionManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        // 1. Cấp phát cho màn hình Hồ sơ chính
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(sessionManager) as T
        }

        // 2. Cấp phát cho màn hình Chỉnh sửa hồ sơ
        if (modelClass.isAssignableFrom(EditProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditProfileViewModel(sessionManager) as T
        }

        // 3. ĐÓN ĐẦU SẴN: Cấp phát cho màn hình Lịch sử nhận nuôi
        if (modelClass.isAssignableFrom(AdoptHistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AdoptHistoryViewModel(sessionManager) as T
        }
        if (modelClass.isAssignableFrom(PostHistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PostHistoryViewModel(sessionManager) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}