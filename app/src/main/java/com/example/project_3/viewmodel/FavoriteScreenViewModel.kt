package com.example.project_3.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3.data.model.Pet
import com.example.project_3.data.remote.RetrofitClient
import kotlinx.coroutines.launch

/**
 * ViewModel chuyên biệt quản lý trạng thái dữ liệu cho màn hình FavoriteScreen.
 */
class FavoriteScreenViewModel : ViewModel() {

    // Danh sách thú cưng yêu thích hỗ trợ tự động cập nhật giao diện khi thêm/xóa phần tử
    var petList = mutableStateListOf<Pet>()
        private set

    // Trạng thái hiển thị vòng xoay tiến trình khi đang kết nối API
    var isLoading = mutableStateOf(false)
        private set

    // Lưu thông tin lỗi nếu quá trình gọi API từ hệ thống thất bại
    var errorMessage = mutableStateOf("")
        private set

    /**
     * Hàm tải danh sách thú cưng yêu thích từ máy chủ theo mã người dùng.
     * @param idUser ID của người dùng hiện tại đang đăng nhập hệ thống.
     */
    fun fetchFavoritePets(idUser: Int) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                errorMessage.value = ""

                // Gọi tới API lấy danh sách từ cơ sở dữ liệu backend
                val response = RetrofitClient.api.getFavoritePets(idUser)

                if (response.success) {
                    petList.clear()
                    petList.addAll(response.pets ?: emptyList())
                } else {
                    errorMessage.value = response.message ?: "Không thể tải danh sách thú cưng."
                }
            } catch (e: Exception) {
                errorMessage.value = "Lỗi kết nối mạng: ${e.localizedMessage}"
            } finally {
                isLoading.value = false
            }
        }
    }

    /**
     * Hàm gửi yêu cầu bật/tắt (Hủy hoặc Thêm) trạng thái theo dõi thú cưng lên máy chủ.
     * @param idUser ID của người dùng.
     * @param idPet ID của thú cưng cần thực hiện thao tác.
     */
    fun followPet(idUser: Int, idPet: Int) {
        viewModelScope.launch {
            try {
                // Gửi lệnh cập nhật dữ liệu xuống cơ sở dữ liệu MySQL thông qua API
                RetrofitClient.api.addFollowPet(idUser, idPet)
            } catch (e: Exception) {
                e.printStackTrace()
                // Có thể tùy biến ghi log hoặc hiển thị lỗi ra màn hình nếu cần thiết
            }
        }
    }
}