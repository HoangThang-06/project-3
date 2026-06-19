package com.example.project_3.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3.data.remote.RetrofitClient
import kotlinx.coroutines.launch

class PetDetailViewModel : ViewModel() {

    var petDetail by mutableStateOf<com.example.project_3.data.model.Pet?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var isSubmitting by mutableStateOf(false)
        private set

    fun loadPetDetail(idPet: Int) {
        if (petDetail?.id_pet == idPet) return
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.api.getPetDetail(idPet)
                if (response.success) {
                    petDetail = response.pet
                } else {
                    petDetail = null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                petDetail = null
            } finally {
                isLoading = false
            }
        }
    }

    /**
     * HÀM CẬP NHẬT CHÍNH XÁC:
     * Truyền trực tiếp Map động lên ApiService (đã đổi sang nhận Any).
     * Đảm bảo chuỗi JSON gửi đi không bị cắt xén mất id_user và id_pet.
     */
    fun registerAdoptionDynamic(
        requestMap: Map<String, Any>,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            isSubmitting = true
            try {
                // Truyền trực tiếp requestMap lên hệ thống mạng
                val response = RetrofitClient.api.registerAdoption(requestMap)

                if (response.success) {
                    onResult(true, response.message ?: "Đăng ký nhận nuôi thành công!")
                } else {
                    onResult(false, response.message ?: "Đăng ký nhận nuôi thất bại.")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false, "Lỗi kết nối hoặc xử lý dữ liệu từ máy chủ.")
            } finally {
                isSubmitting = false
            }
        }
    }
}