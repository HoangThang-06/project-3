package com.example.project_3.data.model

data class AdoptionRequest(
    val id: Int = 0,
    val user_name: String = "",
    val email: String = "",
    val name_pet: String = "",
    val species: String = "",
    val age: String = "",
    val adoption_date: String = "",
    val state: String = "pending" // Trạng thái mặc định
)