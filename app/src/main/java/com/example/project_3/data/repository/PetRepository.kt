package com.example.project_3.data.repository

import com.example.project_3.data.model.PetResponse
import com.example.project_3.data.remote.RetrofitClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class PetRepository {

    // =========================
    // GET ALL PETS
    // =========================
    suspend fun getAllPets(): PetResponse {
        return RetrofitClient.api.getAllPets(-1) // Thêm -1 để khớp với ApiService
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
        imagePart: MultipartBody.Part?, // Nhận từ ViewModel gửi xuống
        age: String,
        species: String
    ): PetResponse {

        // Hàm phụ trợ chuyển chuỗi thông thường thành RequestBody dạng Form-Data text
        fun createPartFromString(string: String): RequestBody {
            return string.toRequestBody("text/plain".toMediaTypeOrNull())
        }

        // Gọi sang ApiService đã sửa ở Bước 1
        return RetrofitClient.api.addPet(
            namePet = createPartFromString(namePet),
            gender = createPartFromString(gender),
            description = createPartFromString(description),
            state = createPartFromString(state),
            image = imagePart,
            age = createPartFromString(age),
            species = createPartFromString(species)
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
            idPet, namePet, gender, description, state, image, age, species
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