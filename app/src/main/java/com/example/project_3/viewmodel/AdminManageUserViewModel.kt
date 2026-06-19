package com.example.project_3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AdminManageUserViewModel : ViewModel() {

    private val userRepository = UserRepository()

    private val _uiState = MutableStateFlow(AdminManageUserUiState())
    val uiState: StateFlow<AdminManageUserUiState> = _uiState.asStateFlow()

    fun loadAllUsers(currentUserId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val response = userRepository.getAllUsers(currentUserId)
                if (response.success && response.users != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            users = response.users,
                            filteredUsers = response.users
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = response.message ?: "Không tải được danh sách") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) }
            }
        }
    }

    fun searchUsers(query: String) {
        _uiState.update { currentState ->
            val filtered = if (query.isEmpty()) {
                currentState.users
            } else {
                currentState.users.filter {
                    // ĐÃ SỬA: Dùng toán tử ?. và kiểm tra == true để xử lý an toàn thuộc tính fullname mang giá trị Null
                    it.fullname?.contains(query, ignoreCase = true) == true ||
                            it.username.contains(query, ignoreCase = true) ||
                            it.email.contains(query, ignoreCase = true)
                }
            }
            currentState.copy(filteredUsers = filtered)
        }
    }

    // Hàm xóa người dùng bằng Repository có sẵn
    fun deleteUser(idUser: String, currentUserId: String) {
        viewModelScope.launch {
            try {
                val response = userRepository.deleteUser(idUser)
                if (response.success) {
                    loadAllUsers(currentUserId)
                } else {
                    _uiState.update { it.copy(errorMessage = response.message) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Không thể xóa: ${e.localizedMessage}") }
            }
        }
    }

    // Hàm cập nhật thông tin người dùng (Sử dụng hàm updateAdminProfile của Repo)
    fun updateUserProfile(
        idUser: Int,
        fullname: String,
        phone: String,
        birthday: String,
        gender: String,
        address: String,
        email: String,
        currentUserId: String
    ) {
        viewModelScope.launch {
            try {
                val response = userRepository.updateAdminProfile(
                    idUser = idUser,
                    fullname = fullname,
                    phone = phone,
                    birthday = birthday,
                    gender = gender,
                    address = address,
                    email = email
                )
                if (response.success) {
                    // Cập nhật thành công thì tự động làm mới lại danh sách ở UI
                    loadAllUsers(currentUserId)
                } else {
                    _uiState.update { it.copy(errorMessage = response.message) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Không thể cập nhật: ${e.localizedMessage}") }
            }
        }
    }
}