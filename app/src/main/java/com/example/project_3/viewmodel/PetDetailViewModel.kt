package com.example.project_3.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3.data.model.AdoptionRequest
import com.example.project_3.data.model.Pet
import com.example.project_3.data.remote.RetrofitClient
import kotlinx.coroutines.launch

class PetDetailViewModel : ViewModel() {

    // Trạng thái lưu trữ thông tin chi tiết thú cưng sau khi gọi API thành công
    var petDetail by mutableStateOf<Pet?>(null)
        private set

    // Trạng thái để theo dõi xem API có đang tải hay gặp lỗi hay không
    var isLoading by mutableStateOf(false)
        private set

    // 🔥 1. BỔ SUNG: Trạng thái loading riêng cho nút Đăng ký nhận nuôi (Tránh bấm lặp)
    var isSubmitting by mutableStateOf(false)
        private set // Chỉ cho phép thay đổi trạng thái từ bên trong ViewModel

    fun loadPetDetail(idPet: Int) {
        // Tránh gọi lại API nếu dữ liệu của thú cưng đó đã tồn tại sẵn
        if (petDetail?.id_pet == idPet) return

        viewModelScope.launch {
            isLoading = true
            try {
                // Gọi API lấy dữ liệu chi tiết từ ApiService
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

    // 🔥 2. BỔ SUNG: Hàm xử lý gọi API gửi thông tin đăng ký nhận nuôi lên Server PHP
    fun submitAdoption(userId: Int, petId: Int, onResult: (String) -> Unit) {
        viewModelScope.launch {
            isSubmitting = true
            try {
                // Gọi hàm registerAdoption từ ApiService thông qua RetrofitClient.api của bạn
                val response = RetrofitClient.api.registerAdoption(
                    AdoptionRequest(id_user = userId, id_pet = petId)
                )
                // Trả chuỗi thông báo (message) từ file PHP về cho View hiển thị Toast
                onResult(response.message)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult("Lỗi kết nối mạng: ${e.message}")
            } finally {
                isSubmitting = false
            }
        }
    }
}