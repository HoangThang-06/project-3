package com.example.project_3.data.model

data class PetDetailResponse(
    val success: Boolean,
    val message: String? = null,
    val pet: Pet? = null
)