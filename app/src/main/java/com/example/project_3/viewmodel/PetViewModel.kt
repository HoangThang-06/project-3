package com.example.project_3.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3.data.model.Pet
import kotlinx.coroutines.launch
import com.example.project_3.data.remote.RetrofitClient

class PetViewModel : ViewModel() {

    // CHỐT CHẶN BẢO MẬT: Ghi nhớ ID người dùng để khi load lại danh sách không bị mất Tim Đỏ
    private var currentSavedUserId: Int = -1

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

    // =========================
    // FETCH ALL PETS (GHI NHỚ ID USER)
    // =========================
    fun fetchPets(idUser: Int = -1) {
        if (idUser != -1) {
            currentSavedUserId = idUser
        }

        viewModelScope.launch {
            try {
                isLoading.value = true
                errorMessage.value = ""

                // Gửi ID người dùng đã lưu lên để kết nối câu lệnh LEFT JOIN phía PHP
                val response = RetrofitClient.api.getAllPets(currentSavedUserId)

                if (response.success) {
                    petList.clear()
                    petList.addAll(response.pets ?: emptyList())
                } else {
                    errorMessage.value = response.message ?: "Không có dữ liệu"
                }
            } catch (e: Exception) {
                errorMessage.value = e.message ?: "Lỗi mạng"
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
                val response = RetrofitClient.api.getTopPet()
                if (response.success) {
                    topPet.value = response.data
                } else {
                    message.value = response.message ?: ""
                }
            } catch (e: Exception) {
                message.value = e.message ?: "Error"
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
                val response = RetrofitClient.api.addPet(
                    namePet, gender, description, state, image, age, species
                )
                message.value = response.message ?: ""

                if (response.success) {
                    fetchPets() // Tự động dùng lại currentSavedUserId đã lưu
                }
            } catch (e: Exception) {
                message.value = e.message ?: "Add failed"
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
                val response = RetrofitClient.api.updatePet(
                    idPet, namePet, gender, description, state, image, age, species
                )
                message.value = response.message ?: ""

                if (response.success) {
                    fetchPets() // Tự động dùng lại currentSavedUserId đã lưu
                }
            } catch (e: Exception) {
                message.value = e.message ?: "Update failed"
            }
        }
    }

    // =========================
    // DELETE PET
    // =========================
    fun deletePet(idPet: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.deletePet(idPet)
                message.value = response.message ?: ""

                if (response.success) {
                    petList.removeAll { it.id_pet.toString() == idPet }
                }
            } catch (e: Exception) {
                message.value = e.message ?: "Delete failed"
            }
        }
    }

    // =========================
    // GET SINGLE PET
    // =========================
    fun getPet(idPet: String) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                val response = RetrofitClient.api.getPet(idPet)

                if (response.success) {
                    pet.value = response.data
                } else {
                    errorMessage.value = response.message ?: "Pet not found"
                }
            } catch (e: Exception) {
                errorMessage.value = e.message ?: "Network error"
            } finally {
                isLoading.value = false
            }
        }
    }

    // =========================
    // FOLLOW / UNFOLLOW PET (TOGGLE ĐỔI MÀU TIM CHUẨN)
    // =========================
    fun followPet(idUser: Int, idPet: Int) {
        currentSavedUserId = idUser // Ghi nhận lại ID người dùng đề phòng

        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.addFollowPet(idUser, idPet)

                if (response.success) {
                    // Tìm đúng vị trí của chú pet vừa nhấn trong danh sách động
                    val index = petList.indexOfFirst { it.id_pet == idPet }
                    if (index != -1) {
                        // Đảo trạng thái: Đỏ (1) thành Xám (0), Xám (0) thành Đỏ (1)
                        val currentStatus = petList[index].isFollowed
                        val newStatus = if (currentStatus == 1) 0 else 1

                        // Cập nhật giá trị mới lập tức lên Jetpack Compose UI
                        val updatedPet = petList[index].copy(isFollowed = newStatus)
                        petList[index] = updatedPet
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    // =========================
    // FETCH FAVORITE PETS
    // =========================
    fun fetchFavoritePets(idUser: Int) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                errorMessage.value = ""

                // Gọi API lấy các pet đã thích
                val response = RetrofitClient.api.getFavoritePets(idUser)

                if (response.success) {
                    petList.clear()
                    petList.addAll(response.pets ?: emptyList())
                } else {
                    errorMessage.value = response.message ?: "Không có dữ liệu"
                }
            } catch (e: Exception) {
                errorMessage.value = e.message ?: "Lỗi mạng hoặc server"
            } finally {
                isLoading.value = false
            }
        }
    }
}