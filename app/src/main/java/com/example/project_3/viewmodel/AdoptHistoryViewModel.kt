package com.example.project_3.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3.data.local.SessionManager
import com.example.project_3.data.model.AdoptionHistory
import com.example.project_3.data.remote.RetrofitClient
import kotlinx.coroutines.launch

class AdoptHistoryViewModel(private val sessionManager: SessionManager) : ViewModel() {

    // Nơi lưu trữ danh sách lịch sử nhận nuôi lấy từ DB về
    var historyList by mutableStateOf<List<AdoptionHistory>>(emptyList())
        private set

    // Trạng thái xoay vòng tròn Loading
    var isLoading by mutableStateOf(false)
        private set

    // Biến thông báo lỗi nếu có
    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        loadAdoptHistory()
    }

    fun loadAdoptHistory() {
        val userId = sessionManager.getUserId()
        if (userId == -1) {
            errorMessage = "Lỗi: Phiên đăng nhập hết hạn!"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                // Gọi API từ RetrofitClient lên server PHP
                val response = RetrofitClient.api.getAdoptHistory(userId)
                if (response.success && response.history != null) {
                    historyList = response.history
                } else {
                    errorMessage = response.message ?: "Không thể tải dữ liệu lịch sử"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage = "Lỗi kết nối Server: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}