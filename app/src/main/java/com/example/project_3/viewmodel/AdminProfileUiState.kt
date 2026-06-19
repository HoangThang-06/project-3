package com.example.project_3.viewmodel

import com.example.project_3.data.model.User

data class AdminProfileUiState(
    val isLoading: Boolean = false,
    val adminInfo: User? = null,
    val errorMessage: String? = null,
    val isLoggedOut: Boolean = false
)