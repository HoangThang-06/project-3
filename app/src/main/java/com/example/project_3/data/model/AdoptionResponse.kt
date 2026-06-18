package com.example.project_3.data.model

data class AdoptionResponse(
    val success: Boolean,
    val message: String
)

data class AdoptionRequest(
    val id_user: Int,
    val id_pet: Int
)