package com.example.project_3.data.repository

import com.example.project_3.data.model.PetResponse
import com.example.project_3.data.remote.RetrofitClient

class PetRepository {

    suspend fun getAllPets(): PetResponse {
        return RetrofitClient.api.getAllPets()
    }
}