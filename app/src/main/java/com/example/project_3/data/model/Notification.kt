package com.example.project_3.data.model
data class Notification(
    val id_notif: Int,
    val user_id_receiver: Int,
    val user_id_sender: Int,
    val article_id: Int?,
    val type: String,        // 'like' hoặc 'comment'
    val is_read: Int,        // 0 hoặc 1
    val create_at: String,
    val sender_name: String, // Lấy từ JOIN users
    val sender_avatar: String // Lấy từ JOIN users
)