package com.example.project_3.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3.data.local.SessionManager
import com.example.project_3.data.model.User
import com.example.project_3.data.repository.SocialRepository // Giả sử bạn để hàm getUser ở đây
import com.example.project_3.data.remote.RetrofitClient
import kotlinx.coroutines.launch

class ProfileViewModel(private val sessionManager: SessionManager) : ViewModel() {
    var user by mutableStateOf<User?>(null)
    var isLoading by mutableStateOf(false)

    init { loadProfile() }

    fun loadProfile() {
        val userId = sessionManager.getUserId()
        if (userId == -1) return // Chưa đăng nhập

        viewModelScope.launch {
            isLoading = true
            try {
                // Gọi API lấy dữ liệu thật
                val response = RetrofitClient.api.getUser(userId.toString())
                if (response.success) {
                    user = response.user
                }
            } catch (e: Exception) { e.printStackTrace() }
            finally { isLoading = false }
        }
    }
}