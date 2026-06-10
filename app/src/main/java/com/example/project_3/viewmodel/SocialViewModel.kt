package com.example.project_3.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3.data.model.Article
import com.example.project_3.data.model.Comment
import com.example.project_3.data.repository.SocialRepository
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class SocialViewModel : ViewModel() {

    // Khởi tạo Repository để làm cầu nối gọi dữ liệu
    private val repository = SocialRepository()

    var articleList = mutableStateListOf<Article>()
        private set

    var isLoading = mutableStateOf(false)
        private set

    var commentList = mutableStateListOf<Comment>()
        private set

    var isCommentsLoading = mutableStateOf(false)
        private set

    var hasMoreComments = mutableStateOf(false)
        private set

    private var currentCommentPage = 1
    private var currentTargetArticleId = -1

    var isPublishing = mutableStateOf(false)
        private set

    // ============================================
    // LOAD ARTICLES QUA REPOSITORY
    // ============================================
    fun loadArticles(userId: Int) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                val response = repository.getArticles(userId) // Gọi qua Repo chuẩn chỉnh
                if (response.success) {
                    articleList.clear()
                    articleList.addAll(response.data)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading.value = false
            }
        }
    }

    // ============================================
    // TOGGLE LIKE ARTICLE QUA REPOSITORY
    // ============================================
    fun toggleLikeArticle(userId: Int, articleId: Int) {
        viewModelScope.launch {
            try {
                // Gọi API xử lý Server thông qua Repo
                val response = repository.toggleLikeArticle(userId, articleId)

                if (response.success) {
                    val index = articleList.indexOfFirst { it.id_article == articleId }
                    if (index != -1) {
                        val currentItem = articleList[index]
                        val newIsLiked = if (currentItem.isLiked == 1) 0 else 1
                        val newLikesCount = if (newIsLiked == 1) {
                            currentItem.likes_count + 1
                        } else {
                            if (currentItem.likes_count > 0) currentItem.likes_count - 1 else 0
                        }

                        val updatedArticle = currentItem.copy(
                            isLiked = newIsLiked,
                            likes_count = newLikesCount
                        )
                        articleList[index] = updatedArticle // Cập nhật UI lập tức
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Gọi hàm này lần đầu khi bấm nút Bình luận
    fun openCommentsForArticle(articleId: Int) {
        currentTargetArticleId = articleId
        currentCommentPage = 1
        commentList.clear() // Xóa danh sách cũ của bài viết trước đi
        loadMoreComments()
    }

    // Gọi hàm này khi bấm vào nút "Xem thêm bình luận"
    fun loadMoreComments() {
        if (currentTargetArticleId == -1 || isCommentsLoading.value) return

        viewModelScope.launch {
            try {
                isCommentsLoading.value = true
                val response = repository.getComments(currentTargetArticleId, currentCommentPage)
                if (response.success) {
                    commentList.addAll(response.data) // Cộng dồn 10 bình luận mới vào danh sách cũ
                    hasMoreComments.value = response.has_more
                    currentCommentPage++ // Tăng số trang lên chuẩn bị cho lần bấm tiếp theo
                } else {
                    hasMoreComments.value = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isCommentsLoading.value = false
            }
        }
    }

    fun sendComment(userId: Int, articleId: Int, content: String, onSendSuccess: () -> Unit) {
        if (content.trim().isEmpty()) return

        viewModelScope.launch {
            try {
                // Gọi qua repository riêng của bạn thay vì RetrofitClient trực tiếp
                val response = repository.addComment(articleId, userId, content)

                if (response.success) {
                    // Thêm ngay bình luận mới vào ĐẦU danh sách hiển thị trên App (Real-time)
                    commentList.add(0, response.comment)

                    // Đồng thời cộng 1 vào số lượng comment của bài viết ở màn hình ngoài
                    val index = articleList.indexOfFirst { it.id_article == articleId }
                    if (index != -1) {
                        articleList[index] = articleList[index].copy(
                            comments_count = articleList[index].comments_count + 1
                        )
                    }
                    onSendSuccess() // Kích hoạt xóa chữ trong ô nhập liệu trên UI
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // ============================================
    // UPLOAD ARTICLE QUA REPOSITORY (Đã sửa lỗi gạch đỏ)
    // ============================================
    fun uploadArticle(
        userId: Int,
        content: String,
        category: String,
        imageBytes: ByteArray?, // Mảng bytes của ảnh lấy từ Uri của thư viện Android
        imageFileName: String?,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        if (content.trim().isEmpty()) {
            onFailure("Nội dung bài viết không được để trống")
            return
        }

        viewModelScope.launch {
            try {
                isPublishing.value = true

                // Đã sửa đổi sang Extension Functions `.toRequestBody` và `.toMediaTypeOrNull()`
                val userIdBody = userId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val contentBody = content.toRequestBody("text/plain".toMediaTypeOrNull())
                val categoryBody = category.toRequestBody("text/plain".toMediaTypeOrNull())

                // Xử lý chuyển đổi mảng byte của ảnh sang MultipartBody.Part
                var imagePart: MultipartBody.Part? = null
                if (imageBytes != null && imageFileName != null) {
                    val requestFile = imageBytes.toRequestBody("image/*".toMediaTypeOrNull())
                    imagePart = MultipartBody.Part.createFormData("image", imageFileName, requestFile)
                }

                val response = repository.addArticle(userIdBody, contentBody, categoryBody, imagePart)
                if (response.success) {
                    loadArticles(userId) // Tải lại danh sách bài viết mới lập tức ở màn hình chính
                    onSuccess()
                } else {
                    onFailure(response.message ?: "Đăng bài thất bại, vui lòng thử lại!")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onFailure("Lỗi kết nối mạng: ${e.message}")
            } finally {
                isPublishing.value = false
            }
        }
    }
}