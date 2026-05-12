package com.example.project_3.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3.data.model.Pet
import com.example.project_3.data.repository.PetRepository
import kotlinx.coroutines.launch

class PetViewModel : ViewModel() {

    private val repository = PetRepository()

    var petList = mutableStateListOf<Pet>()
        private set

    var isLoading = mutableStateOf(false)
        private set

    var errorMessage = mutableStateOf("")
        private set

    init {
        loadPets()
    }

    fun loadPets() {
        viewModelScope.launch {
            try {
                isLoading.value = true

                val response = repository.getAllPets()

                if (response.success) {
                    petList.clear()
                    petList.addAll(response.pets)
                } else {
                    errorMessage.value = "Không có dữ liệu"
                }

            } catch (e: Exception) {
                errorMessage.value = e.message ?: "Lỗi mạng"
            } finally {
                isLoading.value = false
            }
        }
    }
}