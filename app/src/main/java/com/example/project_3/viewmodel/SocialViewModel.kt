package com.example.project_3.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3.data.model.Article
import com.example.project_3.data.repository.SocialRepository // Bạn cần tạo repo này
import kotlinx.coroutines.launch

class SocialViewModel : ViewModel() {
    // Repository này bạn tự tạo tương tự PetRepository nhé
    private val repository = SocialRepository()

    var articleList = mutableStateListOf<Article>()
        private set

    var isLoading = mutableStateOf(false)
        private set

    init {
        loadArticles()
    }

    fun loadArticles() {
        viewModelScope.launch {
            try {
                isLoading.value = true
                val response = repository.getAllArticles() // Gọi get_articles.php
                if (response.success) {
                    articleList.clear()
                    articleList.addAll(response.data)
                }
            } catch (e: Exception) {
                // Xử lý lỗi
            } finally {
                isLoading.value = false
            }
        }
    }

    fun likeArticle(userId: Int, articleId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.likeArticle(userId, articleId) // Gọi like_article.php
                if (response.success) {
                    // Refresh lại danh sách để cập nhật số Like và màu sắc
                    loadArticles()
                }
            } catch (e: Exception) { }
        }
    }
}