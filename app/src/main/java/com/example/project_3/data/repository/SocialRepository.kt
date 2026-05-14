package com.example.project_3.data.repository

import com.example.project_3.data.model.*
import com.example.project_3.data.remote.RetrofitClient

class SocialRepository {
    // Phải chỉ định rõ kiểu trả về là ArticleResponse
    suspend fun getAllArticles(): ArticleResponse {
        return RetrofitClient.api.getAllArticles()
    }

    // Phải chỉ định rõ kiểu trả về là SimpleResponse
    suspend fun likeArticle(userId: Int, articleId: Int): SimpleResponse {
        return RetrofitClient.api.likeArticle(userId, articleId)
    }
}