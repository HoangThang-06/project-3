package com.example.project_3.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3.data.model.Pet
import com.example.project_3.data.repository.PetRepository
import kotlinx.coroutines.launch

class PetViewModel : ViewModel() {

    private val repository = PetRepository()

    // =========================
    // PET LIST
    // =========================

    var petList = mutableStateListOf<Pet>()
        private set

    var pet = mutableStateOf<Pet?>(null)
        private set

    // =========================
    // TOP PET
    // =========================

    var topPet = mutableStateOf<Pet?>(null)
        private set

    // =========================
    // LOADING
    // =========================

    var isLoading = mutableStateOf(false)
        private set

    // =========================
    // ERROR MESSAGE
    // =========================

    var errorMessage = mutableStateOf("")
        private set

    // =========================
    // ACTION MESSAGE
    // =========================

    var message = mutableStateOf("")
        private set

    init {

        loadPets()

    }

    // =========================
    // LOAD ALL PETS
    // =========================

    fun loadPets() {

        viewModelScope.launch {

            try {

                isLoading.value = true

                val response = repository.getAllPets()

                if (response.success) {

                    petList.clear()

                    petList.addAll(
                        response.pets ?: emptyList()
                    )

                } else {

                    errorMessage.value =
                        response.message ?: "Không có dữ liệu"
                }

            } catch (e: Exception) {

                errorMessage.value =
                    e.message ?: "Lỗi mạng"

            } finally {

                isLoading.value = false
            }
        }
    }

    // =========================
    // LOAD TOP PET
    // =========================

    fun loadTopPet() {

        viewModelScope.launch {

            try {

                val response = repository.getTopPet()

                if (response.success) {

                    topPet.value = response.data

                } else {

                    message.value =
                        response.message ?: ""
                }

            } catch (e: Exception) {

                message.value =
                    e.message ?: "Error"
            }
        }
    }

    // =========================
    // ADD PET
    // =========================

    fun addPet(

        namePet: String,
        gender: String,
        description: String,
        state: String,
        image: String,
        age: String,
        species: String

    ) {

        viewModelScope.launch {

            try {

                val response = repository.addPet(
                    namePet,
                    gender,
                    description,
                    state,
                    image,
                    age,
                    species
                )

                message.value =
                    response.message ?: ""

                if (response.success) {

                    loadPets()

                }

            } catch (e: Exception) {

                message.value =
                    e.message ?: "Add failed"
            }
        }
    }

    // =========================
    // UPDATE PET
    // =========================

    fun updatePet(

        idPet: String,
        namePet: String,
        gender: String,
        description: String,
        state: String,
        image: String,
        age: String,
        species: String

    ) {

        viewModelScope.launch {

            try {

                val response = repository.updatePet(
                    idPet,
                    namePet,
                    gender,
                    description,
                    state,
                    image,
                    age,
                    species
                )

                message.value =
                    response.message ?: ""

                if (response.success) {

                    loadPets()

                }

            } catch (e: Exception) {

                message.value =
                    e.message ?: "Update failed"
            }
        }
    }

    // =========================
    // DELETE PET
    // =========================

    fun deletePet(idPet: String) {

        viewModelScope.launch {

            try {

                val response =
                    repository.deletePet(idPet)

                message.value =
                    response.message ?: ""

                if (response.success) {

                    petList.removeAll {

                        it.id_pet.toString() == idPet
                    }
                }

            } catch (e: Exception) {

                message.value =
                    e.message ?: "Delete failed"
            }
        }
    }

    fun getPet(idPet: String) {

        viewModelScope.launch {

            try {

                isLoading.value = true

                val response =
                    repository.getPet(idPet)

                if (response.success) {

                    pet.value = response.data

                } else {

                    errorMessage.value =
                        response.message ?: "Pet not found"
                }

            } catch (e: Exception) {

                errorMessage.value =
                    e.message ?: "Network error"

            } finally {

                isLoading.value = false
            }
        }
    }
}