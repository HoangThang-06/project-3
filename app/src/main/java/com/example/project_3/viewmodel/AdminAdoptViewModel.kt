package com.example.project_3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3.data.model.Pet
import com.example.project_3.data.repository.PetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Cấu trúc dữ liệu hiển thị trên UI
data class AdoptApplication(
    val id: String,
    val applicantName: String,
    val petName: String,
    val petBreed: String,
    val status: String,
    val tags: List<Pair<String, Boolean>>,
    val note: String?,
    val originalPet: Pet
)

data class AdminAdoptUiState(
    val isLoading: Boolean = false,
    val applications: List<AdoptApplication> = emptyList(),
    val searchText: String = "",
    val errorMessage: String? = null,
    val todayProgress: Float = 0.65f
)

class AdminAdoptViewModel(
    private val petRepository: PetRepository = PetRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminAdoptUiState())
    val uiState: StateFlow<AdminAdoptUiState> = _uiState.asStateFlow()

    private var originalList: List<AdoptApplication> = emptyList()

    init {
        loadAdoptApplications()
    }

    fun loadAdoptApplications() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val response = petRepository.getAllPets()

                if (response.success && response.pets != null) {
                    val actualData = response.pets.map { pet ->

                        // 1. Dịch trạng thái (state) từ Database sang Tiếng Việt
                        val vietnameseStatus = when (pet.state.lowercase()) {
                            "available" -> "Sẵn sàng"
                            "reserved" -> "Đang đặt trước"
                            "adopted" -> "Đã nhận nuôi"
                            else -> pet.state
                        }

                        // 2. Dịch giới tính (gender) từ Database sang Tiếng Việt
                        val vietnameseGender = when (pet.gender.lowercase()) {
                            "male" -> "Đực"
                            "female" -> "Cái"
                            else -> pet.gender
                        }

                        // 3. Dịch loài (species) từ Database sang Tiếng Việt
                        val vietnameseSpecies = when (pet.species.lowercase()) {
                            "dog" -> "Chó"
                            "cat" -> "Mèo"
                            "other" -> "Khác"
                            else -> pet.species
                        }

                        AdoptApplication(
                            id = pet.id_pet.toString(),
                            applicantName = "Yêu cầu nhận nuôi",
                            petName = pet.name_pet,
                            petBreed = vietnameseSpecies, // Hiển thị Chó/Mèo thay vì dog/cat
                            status = vietnameseStatus,    // Hiển thị Sẵn sàng/Đã nhận nuôi thay vì available/adopted
                            tags = listOf(
                                "Tuổi: ${pet.age}" to false,
                                vietnameseGender to (pet.gender.lowercase() == "male") // Highlight nếu là giống đực
                            ),
                            note = pet.description,
                            originalPet = pet
                        )
                    }

                    originalList = actualData
                    _uiState.update {
                        it.copy(isLoading = false, applications = actualData)
                    }
                } else {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = response.message ?: "Không có dữ liệu thú cưng trên hệ thống")
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Lỗi kết nối database: ${e.localizedMessage}")
                }
            }
        }
    }

    fun onSearchTextChanged(newText: String) {
        _uiState.update { it.copy(searchText = newText) }

        val filteredList = if (newText.isBlank()) {
            originalList
        } else {
            originalList.filter {
                it.petName.contains(newText, ignoreCase = true) ||
                        it.petBreed.contains(newText, ignoreCase = true) ||
                        it.status.contains(newText, ignoreCase = true)
            }
        }
        _uiState.update { it.copy(applications = filteredList) }
    }

    fun approveApplication(application: AdoptApplication) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val p = application.originalPet
                // Khi duyệt, ta gửi trạng thái 'adopted' khớp với ENUM của Database MySQL
                val response = petRepository.updatePet(
                    idPet = p.id_pet.toString(),
                    namePet = p.name_pet,
                    gender = p.gender,
                    description = p.description,
                    state = "adopted", // <--- GỬI ĐÚNG ENUM DATABASE
                    image = p.image,
                    age = p.age.toString(),
                    species = p.species
                )

                if (response.success) {
                    originalList = originalList.filterNot { it.id == application.id }
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            applications = state.applications.filterNot { it.id == application.id },
                            todayProgress = (state.todayProgress + 0.05f).coerceAtMost(1.0f)
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = response.message) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Lỗi: ${e.localizedMessage}") }
            }
        }
    }

    fun rejectApplication(applicationId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val response = petRepository.deletePet(applicationId)

                if (response.success) {
                    originalList = originalList.filterNot { it.id == applicationId }
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            applications = state.applications.filterNot { it.id == applicationId }
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = response.message) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Lỗi: ${e.localizedMessage}") }
            }
        }
    }
}