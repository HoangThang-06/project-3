package com.example.project_3.data.repository

import com.example.project_3.data.model.AddCommentResponse
import com.example.project_3.data.model.ArticleResponse
import com.example.project_3.data.model.BaseResponse
import com.example.project_3.data.model.CommentResponse
import com.example.project_3.data.remote.RetrofitClient

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
}