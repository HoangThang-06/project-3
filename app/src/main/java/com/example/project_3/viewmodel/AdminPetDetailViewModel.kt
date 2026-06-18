package com.example.project_3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3.data.model.Pet
import com.example.project_3.data.repository.PetRepository
import kotlinx.coroutines.launch

class AdminPetDetailViewModel : ViewModel() {
    private val repository = PetRepository()

    fun updatePet(pet: Pet, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                // Gọi repository update
                repository.updatePet(pet.id_pet, pet.name_pet, pet.gender,
                    pet.description, pet.state, pet.image,
                    pet.age, pet.species)
                onSuccess()
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun deletePet(id: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                repository.deletePet(id) // Nếu Repository nhận Int thì dòng này lỗi
                onSuccess()
            } catch (e: Exception) { e.printStackTrace() }
        }
    }
}