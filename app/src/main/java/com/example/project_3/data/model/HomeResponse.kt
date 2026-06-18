package com.example.project_3.data.model

// Data model đại diện cho Sự kiện (Event)
data class EventModel(
    val title: String,
    val date: String,
    val location: String,
    val image_url: String?
)
data class KnowledgeModel(
    val id_knowledge: Int,
    val title: String,
    val short_description: String,
    val content: String,
    val image: String
)
// Dữ liệu tổng hợp trả về cho trang chủ
data class HomeResponse(
    val success: Boolean,
    val events: List<EventModel>,
    val featured_pets: List<Pet>, // Tái sử dụng Class Pet có sẵn của bạn
    val knowledge: List<KnowledgeModel>
)