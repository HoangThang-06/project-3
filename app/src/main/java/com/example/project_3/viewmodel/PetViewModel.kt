package com.example.project_3.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3.data.model.Pet
import com.example.project_3.data.repository.PetRepository
import com.example.project_3.data.remote.RetrofitClient
import kotlinx.coroutines.launch

class PetViewModel : ViewModel() {

    private val petRepository = PetRepository()

    var petList = mutableStateListOf<Pet>()
        private set

    var pet = mutableStateOf<Pet?>(null)
        private set

    var isLoading = mutableStateOf(false)
        private set

    var errorMessage = mutableStateOf("")
        private set

    /**
     * SỬA TẠI ĐÂY: Đổi tên từ fetchAllPets thành fetchPets
     * để khớp với AdoptScreen
     */
    fun fetchPets(idUser: Int) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                errorMessage.value = ""

                // Giả sử logic lấy danh sách thú cưng
                val response = petRepository.getAllPets()
                if (response.success && response.pets != null) {
                    petList.clear()
                    petList.addAll(response.pets)
                } else {
                    errorMessage.value = response.message ?: "Không lấy được danh sách"
                }
            } catch (e: Exception) {
                errorMessage.value = "Lỗi kết nối: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }

    /**
     * SỬA TẠI ĐÂY: Đổi tên từ toggleFollowPet thành followPet
     * để khớp với AdoptScreen
     */
    fun followPet(idUser: Int, idPet: Int) {
        viewModelScope.launch {
            try {
                // Gọi API cập nhật trạng thái
                RetrofitClient.api.addFollowPet(idUser, idPet)

                // Cập nhật UI ngay lập tức
                val index = petList.indexOfFirst { it.id_pet == idPet }
                if (index != -1) {
                    val currentStatus = petList[index].isFollowed
                    val newStatus = if (currentStatus == 1) 0 else 1
                    val updatedPet = petList[index].copy(isFollowed = newStatus)
                    petList[index] = updatedPet
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchFavoritePets(idUser: Int) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                errorMessage.value = ""
                val response = RetrofitClient.api.getFavoritePets(idUser)
                if (response.success) {
                    petList.clear()
                    petList.addAll(response.pets ?: emptyList())
                } else {
                    errorMessage.value = response.message ?: "Không có dữ liệu thú cưng yêu thích"
                }
            } catch (e: Exception) {
                errorMessage.value = e.message ?: "Lỗi kết nối Server"
            } finally {
                isLoading.value = false
            }
        }
    }
}