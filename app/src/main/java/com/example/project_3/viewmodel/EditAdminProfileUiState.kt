package com.example.project_3.viewmodel

// Định nghĩa trạng thái hiển thị của màn hình chỉnh sửa
data class EditAdminProfileUiState(
    val isLoading: Boolean = false,
    val isUpdateSuccess: Boolean = false,
    val errorMessage: String? = null
)