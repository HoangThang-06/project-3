package com.example.project_3.data.model

data class NotificationResponse(
    val success: Boolean, // Phải có dòng này
    val data: List<Notification>
)