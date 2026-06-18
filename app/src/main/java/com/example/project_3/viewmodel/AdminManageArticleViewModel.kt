package com.example.project_3.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3.data.model.Article
import com.example.project_3.data.repository.SocialRepository
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class AdminManageArticleViewModel : ViewModel() {

    private val repository = SocialRepository()

    // Danh sách bài viết hiển thị trên giao diện Admin
    var articleList = mutableStateListOf<Article>()
        private set

    var isLoading = mutableStateOf(false)
        private set

    // Biến lưu thông báo phản hồi (Toast/Dialog)
    var messageNotification = mutableStateOf<String?>(null)
        private set

    init {
        // Khi mở màn hình Admin, mặc định truyền userId = 0 để API get_articles.php biết đây là Admin và trả về TẤT CẢ các bài viết (cả public lẫn private)
        loadAdminArticles()
    }
    var selectedArticle by mutableStateOf<Article?>(null)

    fun selectArticleForEdit(article: Article) {
        selectedArticle = article
    }

    fun loadAdminArticles() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = repository.getArticles(userId = 0)
                if (response.success) {
                    articleList.clear()
                    articleList.addAll(response.data)
                } else {
                    articleList.clear()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading.value = false
            }
        }
    }

    // 1. Chức năng DUYỆT hoặc ẨN NHANH bài viết (Cập nhật Status)
    fun updateArticleStatus(idArticle: Int, currentStatus: String) {
        viewModelScope.launch {
            // Logic: Nếu đang public thì ẩn đi (private), nếu đang private thì duyệt cho hiển thị (public)
            val nextStatus = if (currentStatus == "public") "private" else "public"
            try {
                val response = repository.updateArticleStatus(idArticle, nextStatus)
                if (response.success) {
                    messageNotification.value = "Thay đổi trạng thái thành công!"
                    loadAdminArticles() // Tải lại danh sách mới
                } else {
                    messageNotification.value = "Thất bại: ${response.message}"
                }
            } catch (e: Exception) {
                messageNotification.value = "Lỗi kết nối mạng"
            }
        }
    }

    // 2. Chức năng SỬA thông tin chi tiết bài viết
    fun updateArticleContent(idArticle: Int, title: String, content: String, image: String, category: String, status: String) {
        viewModelScope.launch {
            try {
                val response = repository.updateArticle(idArticle, title, content, image, category, status)
                if (response.success) {
                    messageNotification.value = "Cập nhật bài viết thành công!"
                    loadAdminArticles()
                } else {
                    messageNotification.value = response.message
                }
            } catch (e: Exception) {
                messageNotification.value = "Lỗi hệ thống"
            }
        }
    }

    // 3. Chức năng XÓA bài viết hoàn toàn (Xóa luôn cả Likes và Comments liên quan ở DB)
    fun deleteArticle(idArticle: Int) {
        viewModelScope.launch {
            try {
                val response = repository.deleteArticle(idArticle)
                if (response.success) {
                    messageNotification.value = "Đã xóa bài viết khỏi hệ thống!"
                    loadAdminArticles()
                } else {
                    messageNotification.value = response.message
                }
            } catch (e: Exception) {
                messageNotification.value = "Lỗi khi xóa dữ liệu"
            }
        }
    }

    fun clearNotification() {
        messageNotification.value = null
    }
}