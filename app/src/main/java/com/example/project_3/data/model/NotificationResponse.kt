package com.example.project_3.data.model

data class DbNotification(
    val id_notif: Int,
    val article_id: Int,
    val type: String,
    val is_read: Int, // 0: Chưa đọc, 1: Đã đọc
    val create_at: String,
    val content: String // Nội dung đã được sinh từ PHP: "X đã thích bài viết của bạn"
)

data class NotificationResponse(
    val success: Boolean,
    val unread_count: Int,
    val notifications: List<DbNotification>?
)