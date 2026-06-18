package com.example.project_3.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3.data.model.DbNotification
import com.example.project_3.data.model.EventModel
import com.example.project_3.data.model.KnowledgeModel
import com.example.project_3.data.model.Pet
import com.example.project_3.data.repository.HomeRepository
import kotlinx.coroutines.launch
import java.util.Calendar

class HomeViewModel : ViewModel() {

    // CHUẨN FORM 1: Tự khởi tạo Repository giống hệt SocialViewModel để không bị crash app
    private val homeRepository = HomeRepository()

    // CHUẨN FORM 2: Áp dụng 'private set' để bảo vệ luồng dữ liệu state
    var greetingText = mutableStateOf("Chào bạn! 👋")
        private set

    var eventsList = mutableStateOf<List<EventModel>>(emptyList())
        private set

    var featuredPets = mutableStateOf<List<Pet>>(emptyList())
        private set

    var knowledgeList = mutableStateOf<List<KnowledgeModel>>(emptyList())
        private set

    var isLoading = mutableStateOf(false)
        private set

    var unreadNotificationCount = mutableStateOf(0)
        private set

    var dbNotificationList = mutableStateOf<List<DbNotification>>(emptyList())
        private set

    init {
        updateGreetingBasedOnTime()
        // Tạm thời load dữ liệu tổng trang chủ khi init
        loadHomeData()
    }

    // Đọc thời gian thực tế trên thiết bị để đưa ra câu chào chính xác
    private fun updateGreetingBasedOnTime() {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        greetingText.value = when (hour) {
            in 4..10 -> "Chào buổi sáng, Bạn! 👋"
            in 11..17 -> "Chào buổi chiều, Bạn! 👋"
            else -> "Chào buổi tối, Bạn! 👋"
        }
    }

    // ============================================
    // LOAD DỮ LIỆU TRANG CHỦ QUA REPOSITORY
    // ============================================
    fun loadHomeData() {
        viewModelScope.launch { // Chạy mặc định trên Main Thread, không dùng Dispatchers.IO thủ công
            try {
                isLoading.value = true

                // CHUẨN FORM 3: Gọi qua cửa ngõ Repository thay vì gọi trực tiếp RetrofitClient
                val response = homeRepository.getHomeDataFromServer()

                if (response.success) {
                    eventsList.value = response.events ?: emptyList()
                    featuredPets.value = response.featured_pets ?: emptyList()
                    knowledgeList.value = response.knowledge ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading.value = false
            }
        }
    }

    // ============================================
    // FETCH THÔNG BÁO TỪ SERVER QUA REPOSITORY
    // ============================================
    fun fetchNotifications(userId: Int) {
        viewModelScope.launch {
            try {
                // Nhờ cậy Repository đi lấy dữ liệu qua mạng sạch sẽ
                val response = homeRepository.getNotificationsFromServer(userId)

                if (response.success) {
                    unreadNotificationCount.value = response.unread_count
                    dbNotificationList.value = response.notifications ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}