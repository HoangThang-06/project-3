package com.example.project_3.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3.data.local.SessionManager
import com.example.project_3.data.model.User
import com.example.project_3.data.remote.RetrofitClient
import kotlinx.coroutines.launch

class ProfileViewModel(val sessionManager: SessionManager) : ViewModel() { // Đổi private val thành val để Screen có thể gọi nếu cần

    // Khởi tạo State lấy trực tiếp dữ liệu từ SessionManager ra trước để màn hình có data hiển thị ngay, không bị trắng
    var user by mutableStateOf<User?>(sessionManager.getUser())
        private set

    var isLoading by mutableStateOf(false)
        private set

    init {
        loadProfile()
    }

    // =========================================================================
    // THÊM HÀM NÀY: Dùng để cập nhật giao diện ngay lập tức khi từ màn hình Edit quay về
    // =========================================================================
    fun refreshUser() {
        user = sessionManager.getUser()
    }

    fun loadProfile() {
        // Lấy thông tin user cơ bản đã lưu tạm trong Session khi Đăng nhập thành công
        val localUser = sessionManager.getUser()

        // Nếu không lấy được ID người dùng (chưa đăng nhập hoặc session rỗng) thì dừng lại
        val userId = localUser?.id_user ?: return

        viewModelScope.launch {
            isLoading = true
            try {
                // Gọi API lấy dữ liệu thật từ file PHP gộp
                val response = RetrofitClient.api.getUser(userId.toString())

                if (response.success && response.user != null) {
                    user = response.user

                    // Cập nhật ngược lại SessionManager để lưu trữ số lượng bài viết / adopt mới nhất xuống bộ nhớ tạm của máy
                    sessionManager.saveUser(response.user)
                } else {
                    // Nếu API thất bại (hoặc phản hồi lỗi), dùng tạm dữ liệu cũ lưu ở Session để tránh màn hình bị trắng
                    user = localUser
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Khi mất mạng hoặc server sập, giữ lại giao diện cũ cho người dùng trải nghiệm tốt hơn
                user = localUser
            } finally {
                isLoading = false
            }
        }
    }
}