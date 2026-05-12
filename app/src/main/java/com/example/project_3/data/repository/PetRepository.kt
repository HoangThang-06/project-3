package com.example.project_3.repository

import com.example.project_3.api.RetrofitClient

class PetRepository {

    suspend fun getAllPets() =
        RetrofitClient.petApi.getAllPets()
}
