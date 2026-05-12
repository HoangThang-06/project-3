package com.example.project_3.data.remote

import com.example.project_3.model.PetResponse
import retrofit2.http.GET

interface PetApi {

    @GET("api/pet/get_all_pets.php")
    suspend fun getAllPets(): PetResponse
}