package com.example.project_3.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3.model.Pet
import com.example.project_3.repository.PetRepository
import kotlinx.coroutines.launch

class PetViewModel : ViewModel() {

    private val repository = PetRepository()

    var petList = mutableStateListOf<Pet>()
        private set

    var isLoading = false
    var errorMessage = ""

    init {
        loadPets()
    }

    fun loadPets() {

        viewModelScope.launch {

            try {

                isLoading = true

                val response = repository.getAllPets()

                if (response.success) {

                    petList.clear()
                    petList.addAll(response.pets)

                } else {
                    errorMessage = "Không có dữ liệu"
                }

            } catch (e: Exception) {

                errorMessage = e.message.toString()

            } finally {

                isLoading = false
            }
        }
    }
}