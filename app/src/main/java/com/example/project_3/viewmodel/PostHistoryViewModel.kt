package com.example.project_3.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3.data.local.SessionManager
import com.example.project_3.data.model.UserPost
import com.example.project_3.data.remote.RetrofitClient
import kotlinx.coroutines.launch

class PostHistoryViewModel(private val sessionManager: SessionManager) : ViewModel() {

    var postList by mutableStateOf<List<UserPost>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        loadPostHistory()
    }

    fun loadPostHistory() {
        val userId = sessionManager.getUserId()
        if (userId == -1) {
            errorMessage = "Phiên đăng nhập hết hạn"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                // Gọi tới endpoint API vừa viết
                val response = RetrofitClient.api.getPostHistory(userId)
                if (response.success && response.posts != null) {
                    postList = response.posts
                } else {
                    errorMessage = response.message ?: "Không lấy được dữ liệu"
                }
            } catch (e: Exception) {
                errorMessage = "Lỗi kết nối: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
    fun updatePost(idArticle: Int, action: String, onComplete: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.updatePostStatus(idArticle, action)
                if (response.success) {
                    // Thao tác thành công thì load lại danh sách bài viết để cập nhật giao diện lập tức
                    loadPostHistory()
                    onComplete(response.message ?: "Thành công")
                } else {
                    onComplete(response.message ?: "Thất bại")
                }
            } catch (e: Exception) {
                onComplete("Lỗi kết nối: ${e.message}")
            }
        }
    }
}