package com.example.project_3.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3.data.model.Article
import com.example.project_3.data.repository.SocialRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminArticleUiState(
    val isLoading: Boolean = false,
    val articles: List<Article> = emptyList(),
    val errorMessage: String? = null
)

class AdminManageArticleViewModel : ViewModel() {

    private val repository = SocialRepository()

    private val _uiState = MutableStateFlow(AdminArticleUiState())
    val uiState: StateFlow<AdminArticleUiState> = _uiState.asStateFlow()

    var messageNotification = mutableStateOf<String?>(null)
        private set

    // Quản lý bài viết đang được chọn để sửa/xem chi tiết
    var selectedArticle by mutableStateOf<Article?>(null)

    // --- CÁC TRẠNG THÁI PHỤC VỤ CHO FORM CHỈNH SỬA CHI TIẾT ---
    var editTitle by mutableStateOf("")
    var editContent by mutableStateOf("")
    var editCategory by mutableStateOf("")
    var editStatus by mutableStateOf("public")
    var editImageUrl by mutableStateOf("")

    init {
        loadAdminArticles()
    }

    // Hàm chọn bài viết và chuẩn bị dữ liệu đổ vào Form chỉnh sửa
    fun selectArticleForEdit(article: Article) {
        selectedArticle = article
        editTitle = article.title
        editContent = article.content
        editCategory = article.category
        editStatus = article.status
        editImageUrl = article.image
    }

    fun loadAdminArticles() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val response = repository.getArticles(0) // 0 tức là Admin, lấy hết
                if (response.success) {
                    _uiState.update { it.copy(isLoading = false, articles = response.data) }
                } else {
                    // Sửa response.message thành chuỗi thông báo rõ ràng để tránh Unresolved reference
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Không thể lấy danh sách bài viết") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Lỗi kết nối hệ thống") }
            }
        }
    }

    // Cập nhật trạng thái nhanh ẩn/hiện ngoài danh sách
    fun toggleArticleStatus(article: Article) {
        viewModelScope.launch {
            try {
                val newStatus = if (article.status == "public") "private" else "public"
                val response = repository.updateArticleStatus(article.id_article, newStatus)
                if (response.success) {
                    messageNotification.value = "Đã chuyển bài viết sang trạng thái: $newStatus"
                    loadAdminArticles()
                } else {
                    messageNotification.value = "Cập nhật trạng thái thất bại từ hệ thống"
                }
            } catch (e: Exception) {
                messageNotification.value = "Không thể cập nhật trạng thái bài viết"
            }
        }
    }

    // Hàm THỰC THI lưu chỉnh sửa chi tiết bài viết về Server PHP
    fun updateArticleContent(idArticle: Int) {
        if (editTitle.isBlank() || editContent.isBlank()) {
            messageNotification.value = "Tiêu đề và nội dung không được để trống!"
            return
        }

        viewModelScope.launch {
            try {
                val response = repository.updateArticle(
                    idArticle = idArticle,
                    title = editTitle,
                    content = editContent,
                    image = editImageUrl, // Giữ lại link ảnh cũ hoặc chuỗi ảnh mới
                    category = editCategory,
                    status = editStatus
                )
                if (response.success) {
                    messageNotification.value = "Cập nhật bài viết thành công!"
                    loadAdminArticles() // Tải lại danh sách mới nhất
                } else {
                    // Sửa response.message thành chuỗi thông báo cố định
                    messageNotification.value = "Cập nhật bài viết thất bại"
                }
            } catch (e: Exception) {
                messageNotification.value = "Lỗi hệ thống khi sửa bài viết"
            }
        }
    }

    fun deleteArticle(idArticle: Int) {
        viewModelScope.launch {
            try {
                val response = repository.deleteArticle(idArticle)
                if (response.success) {
                    messageNotification.value = "Đã xóa bài viết khỏi hệ thống!"
                    loadAdminArticles()
                } else {
                    messageNotification.value = "Xóa bài viết thất bại"
                }
            } catch (e: Exception) {
                messageNotification.value = "Lỗi hệ thống khi xóa bài viết"
            }
        }
    }

    fun clearNotification() {
        messageNotification.value = null
    }

    // Thêm chức năng duyệt trạng thái nhanh công khai/riêng tư (public/private)
    fun updateArticleStatus(idArticle: Int, currentStatus: String) {
        viewModelScope.launch {
            // Đảo ngược trạng thái hiện tại của bài viết
            val newStatus = if (currentStatus == "public") "private" else "public"
            try {
                val response = repository.updateArticleStatus(idArticle, newStatus)
                if (response.success) {
                    messageNotification.value = "Cập nhật trạng thái bài viết thành công!"
                    loadAdminArticles() // Tải lại danh sách bài viết mới nhất
                } else {
                    messageNotification.value = response.message ?: "Cập nhật trạng thái thất bại"
                }
            } catch (e: Exception) {
                messageNotification.value = "Không thể cập nhật trạng thái bài viết"
            }
        }
    }
}