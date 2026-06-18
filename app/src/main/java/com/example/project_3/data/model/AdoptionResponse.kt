package com.example.project_3.data.model

data class AdoptionResponse(
    val success: Boolean,
<<<<<<< HEAD
    val message: String
)

data class AdoptionRequest(
    val id_user: Int,
    val id_pet: Int
=======
    val message: String,
    val data: List<AdoptionRequest>? = null
>>>>>>> d1deb52932fa474fa7903bc41a441996ba4a5ce1
)