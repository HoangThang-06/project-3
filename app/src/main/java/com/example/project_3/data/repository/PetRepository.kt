package com.example.project_3.data.repository

import com.example.project_3.data.model.PetResponse
import com.example.project_3.data.remote.RetrofitClient

class PetRepository {

    // =========================
    // GET ALL PETS
    // =========================

    suspend fun getAllPets(): PetResponse {

        return RetrofitClient.api.getAllPets()

    }

    suspend fun getPet(
        idPet: String
    ): PetResponse {

        return RetrofitClient.api.getPet(idPet)
    }

    // =========================
    // GET TOP PET
    // =========================

    suspend fun getTopPet(): PetResponse {

        return RetrofitClient.api.getTopPet()

    }

    // =========================
    // ADD PET
    // =========================

    suspend fun addPet(

        namePet: String,
        gender: String,
        description: String,
        state: String,
        image: String,
        age: String,
        species: String

    ): PetResponse {

        return RetrofitClient.api.addPet(
            namePet,
            gender,
            description,
            state,
            image,
            age,
            species
        )
    }

    // =========================
    // UPDATE PET
    // =========================

    suspend fun updatePet(

        idPet: String,
        namePet: String,
        gender: String,
        description: String,
        state: String,
        image: String,
        age: String,
        species: String

    ): PetResponse {

        return RetrofitClient.api.updatePet(
            idPet,
            namePet,
            gender,
            description,
            state,
            image,
            age,
            species
        )
    }

    // =========================
    // DELETE PET
    // =========================

    suspend fun deletePet(
        idPet: String
    ): PetResponse {

        return RetrofitClient.api.deletePet(idPet)

    }
}