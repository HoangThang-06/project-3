package com.example.project_3.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3.data.model.Pet
import com.example.project_3.data.remote.RetrofitClient // Thay thế bằng object Retrofit của bạn
import kotlinx.coroutines.launch

class PetDetailViewModel : ViewModel() {

    // Trạng thái lưu trữ thông tin chi tiết thú cưng sau khi gọi API thành công
    var petDetail by mutableStateOf<Pet?>(null)
        private set

    // Trạng thái để theo dõi xem API có đang tải hay gặp lỗi hay không
    var isLoading by mutableStateOf(false)
        private set

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
}