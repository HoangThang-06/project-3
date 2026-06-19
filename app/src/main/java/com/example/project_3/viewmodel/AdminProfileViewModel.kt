package com.example.project_3.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.project_3.data.local.SessionManager

class AdminProfileViewModel : ViewModel() {
    private val _editUiState = MutableStateFlow(EditAdminProfileUiState())
    val editUiState: StateFlow<EditAdminProfileUiState> = _editUiState.asStateFlow()
    private val userRepository = UserRepository()

    private val _uiState = MutableStateFlow(AdminProfileUiState())
    val uiState: StateFlow<AdminProfileUiState> = _uiState.asStateFlow()

    var toastMessage = mutableStateOf<String?>(null)
        private set

    fun loadAdminProfile(adminId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val response = userRepository.getUser(adminId)
                // KHỚP CHUẨN XÁC VỚI PHẢN HỒI USERRESPONSE TỪ USERREPOSITORY CỦA BẠN
                if (response.success && response.user != null) {
                    val userDetail = response.user
                    if (userDetail.role == "admin") {
                        _uiState.update { it.copy(isLoading = false, adminInfo = userDetail) }
                    } else {
                        _uiState.update { it.copy(isLoading = false, errorMessage = "Tài khoản không có quyền quản trị viên!") }
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = response.message ?: "Không lấy được thông tin") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Lỗi kết nối server: ${e.localizedMessage}") }
            }
        }
    }

    fun logout(sessionManager: SessionManager) {
        viewModelScope.launch {
            // 1. Gọi hàm logout của SessionManager để xóa sạch SharedPreferences
            sessionManager.logout()

            // 2. Cập nhật trạng thái UI State để báo hiệu cho View biết đã logout thành công
            _uiState.update { it.copy(isLoggedOut = true) }
            toastMessage.value = "Đăng xuất thành công!"
        }
    }

    fun clearToast() {
        toastMessage.value = null
    }

    fun updateAdminProfile(
        adminId: String,
        fullname: String,
        phone: String,
        birthday: String,
        gender: String,
        address: String,
        avatar: String,  // Tham số này nhận từ UI, tạm thời giữ để không lỗi giao diện
        email: String
    ) {
        viewModelScope.launch {
            _editUiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                // Ép kiểu adminId từ String sang Int để khớp với hàm updateProfile trong ApiService
                val adminIdInt = adminId.toIntOrNull() ?: 0

                // Thay vì gọi updateUser (bị 404), ta gọi hàm kết nối tới "user/update_profile.php"
                // Bạn có thể gọi trực tiếp qua apiService hoặc qua phương thức cầu nối của userRepository tùy cấu trúc dự án
                val response = userRepository.updateAdminProfile(
                    idUser = adminIdInt,
                    fullname = fullname,
                    phone = phone,
                    birthday = birthday,
                    gender = gender,
                    address = address,
                    email = email
                )

                if (response.success) {
                    _editUiState.update { it.copy(isLoading = false, isUpdateSuccess = true) }
                    toastMessage.value = "Cập nhật thông tin thành công!"
                    loadAdminProfile(adminId) // Tải lại dữ liệu mới lên UI chính
                } else {
                    _editUiState.update { it.copy(isLoading = false, errorMessage = response.message) }
                }
            } catch (e: Exception) {
                _editUiState.update { it.copy(isLoading = false, errorMessage = "Lỗi hệ thống: ${e.localizedMessage}") }
            }
        }
    }

    // Hàm reset trạng thái thành công để tránh lặp lệnh quay về màn hình cũ
    fun resetUpdateStatus() {
        _editUiState.update { it.copy(isUpdateSuccess = false, errorMessage = null) }
    }
}