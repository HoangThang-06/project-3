package com.example.project_3.data.model

data class AdoptionRequest(
    val id: Int,
    val id_user: Int,
    val user_name: String,
    val email: String,
    val id_pet: Int,
    val name_pet: String,
    val species: String,
    val age: Int,
    val adoption_date: String,
    val state: String // Trạng thái của đơn: pending, approved, rejected
)