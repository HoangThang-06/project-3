package com.example.project_3.data.repository

import com.example.project_3.data.model.AddCommentResponse
import com.example.project_3.data.model.ArticleResponse
import com.example.project_3.data.model.BaseResponse
import com.example.project_3.data.model.CommentResponse
import com.example.project_3.data.remote.RetrofitClient
import okhttp3.MultipartBody
import okhttp3.RequestBody

class SocialRepository {

    // Lấy danh sách bài viết (Truyền kèm userId để check trạng thái tim đỏ/xám)
    suspend fun getArticles(userId: Int): ArticleResponse {
        return RetrofitClient.api.getArticles(userId)
    }

    // Xử lý Thích / Bỏ thích bài viết real-time (Sử dụng BaseResponse đồng bộ với ApiService)
    suspend fun toggleLikeArticle(userId: Int, articleId: Int): BaseResponse {
        return RetrofitClient.api.toggleLikeArticle(userId, articleId)
    }

    suspend fun getComments(articleId: Int, page: Int): CommentResponse {
        return RetrofitClient.api.getComments(articleId, page)
    }

    // Thêm vào trong class SocialRepository
    suspend fun addComment(articleId: Int, userId: Int, content: String): AddCommentResponse {
        return RetrofitClient.api.addComment(articleId, userId, content)
    }

    suspend fun addArticle(
        userId: RequestBody,
        content: RequestBody,
        category: RequestBody,
        image: MultipartBody.Part?
    ): BaseResponse {
        return RetrofitClient.api.addArticle(userId, content, category, image)
    }

    // ==========================================
    // THÊM MỚI CÁC HÀM PHỤC VỤ ADMIN SOCIAL
    // ==========================================

    // Hàm gọi API duyệt trạng thái nhanh (status nhận 'public' hoặc 'private')
    suspend fun updateArticleStatus(idArticle: Int, status: String): BaseResponse {
        return RetrofitClient.api.updateArticleStatus(idArticle, status)
    }

    // Hàm gọi API sửa nội dung thông tin chi tiết bài viết
    suspend fun updateArticle(
        idArticle: Int,
        title: String,
        content: String,
        image: String,
        category: String,
        status: String
    ): BaseResponse {
        return RetrofitClient.api.updateArticle(idArticle, title, content, image, category, status)
    }

    // Hàm gọi API xóa bài viết hoàn toàn khỏi cơ sở dữ liệu
    suspend fun deleteArticle(idArticle: Int): BaseResponse {
        return RetrofitClient.api.deleteArticle(idArticle)
    }
}