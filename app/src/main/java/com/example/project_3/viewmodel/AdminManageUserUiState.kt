package com.example.project_3.viewmodel

import com.example.project_3.data.model.User

data class AdminManageUserUiState(
    val isLoading: Boolean = false,
    val users: List<User> = emptyList(),
    val filteredUsers: List<User> = emptyList(),
    val errorMessage: String? = null,
    val isActionSuccess: Boolean = false
)