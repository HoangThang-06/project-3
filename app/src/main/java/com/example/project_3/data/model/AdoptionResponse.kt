package com.example.project_3.data.model

data class AdoptionResponse(
    val success: Boolean,
    val message: String,
    val data: List<AdoptionRequest>? = null
)