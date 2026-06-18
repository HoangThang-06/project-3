package com.example.project_3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3.data.model.Pet
import com.example.project_3.data.repository.PetRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody

class AdminManagePetViewModel : ViewModel() {

    private val repository = PetRepository()

    // Danh sách gốc từ API
    private val _allPets = MutableStateFlow<List<Pet>>(emptyList())

    // Danh sách sau khi áp dụng Filter (Tìm kiếm, Trạng thái)
    private val _filteredPets = MutableStateFlow<List<Pet>>(emptyList())
    val filteredPets: StateFlow<List<Pet>> = _filteredPets

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private var currentSearchQuery = ""
    private var currentStatusFilter = "All"

    init {
        fetchPets()
    }

    // 1. LẤY DANH SÁCH THÚ CƯNG
    fun fetchPets() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = withContext(Dispatchers.IO) {
                    repository.getAllPets()
                }
                // Khớp với cấu trúc PetResponse (lấy trường .pets)
                val pets: List<Pet> = response.pets ?: emptyList()
                _allPets.value = pets
                applyFilters()
            } catch (e: Exception) {
                e.printStackTrace()
                _allPets.value = emptyList()
                _filteredPets.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // 3. CẬP NHẬT TRẠNG THÁI THÚ CƯNG (QUẢN LÝ)
    fun updatePetState(pet: Pet, newState: String) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    repository.updatePet(
                        idPet = pet.id_pet.toString(),
                        namePet = pet.name_pet,
                        gender = pet.gender,
                        description = pet.description,
                        state = newState,
                        image = pet.image,
                        age = pet.age.toString(),
                        species = pet.species
                    )
                }
                if (response.success) {
                    fetchPets()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // 4. XÓA THÚ CƯNG
    fun deletePet(idPet: String) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    repository.deletePet(idPet)
                }
                if (response.success) {
                    fetchPets()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // XỬ LÝ SEARCH & FILTER
    fun updateSearchQuery(query: String) {
        currentSearchQuery = query
        applyFilters()
    }

    fun updateStatusFilter(status: String) {
        currentStatusFilter = status
        applyFilters()
    }

    private fun applyFilters() {
        var list = _allPets.value

        if (currentStatusFilter != "All") {
            list = list.filter { it.state.lowercase() == currentStatusFilter.lowercase() }
        }

        if (currentSearchQuery.isNotEmpty()) {
            list = list.filter { it.name_pet.contains(currentSearchQuery, ignoreCase = true) }
        }

        _filteredPets.value = list
    }
}