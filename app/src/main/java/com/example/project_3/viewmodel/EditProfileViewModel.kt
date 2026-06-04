package com.example.project_3.viewmodel

import android.util.Log // Import để theo dõi logcat cực kỳ quan trọng
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3.data.local.SessionManager
import com.example.project_3.data.remote.RetrofitClient
import kotlinx.coroutines.delay // Thêm delay để đợi hiển thị Toast
import kotlinx.coroutines.launch

class EditProfileViewModel(private val sessionManager: SessionManager) : ViewModel() {

    val currentUser = sessionManager.getUser()

    // Quản lý trạng thái các ô nhập liệu (Form States)
    var fullname by mutableStateOf(currentUser?.fullname ?: "")
    var phone by mutableStateOf(currentUser?.phone ?: "")
    var birthday by mutableStateOf(currentUser?.birthday ?: "")
    var gender by mutableStateOf(currentUser?.gender ?: "Nam")
    var address by mutableStateOf(currentUser?.address ?: "")
    var email by mutableStateOf(currentUser?.email ?: "")

    var isLoading by mutableStateOf(false)
    var updateResult by mutableStateOf<String?>(null)

    fun updateProfile(onSuccess: () -> Unit) {
        val userId = currentUser?.id_user ?: -1

        // Kiểm tra xem có lấy được ID người dùng đăng nhập không
        if (userId == -1) {
            updateResult = "Lỗi: Không tìm thấy phiên đăng nhập!"
            Log.e("EDIT_PROFILE_VM", "Không tìm thấy id_user trong SessionManager")
            return
        }

        if (fullname.isBlank()) {
            updateResult = "Họ tên không được để trống"
            return
        }

        viewModelScope.launch {
            isLoading = true
            Log.d("EDIT_PROFILE_VM", "Bắt đầu gửi dữ liệu lên API cho id_user: $userId")
            Log.d("EDIT_PROFILE_VM", "Data gửi đi: fullname=$fullname, phone=$phone, birthday=$birthday, gender=$gender, address=$address, email=$email")

            try {
                // Gọi API truyền đúng các trường thông tin chữ xuống PHP
                val response = RetrofitClient.api.updateProfile(
                    idUser = userId,
                    fullname = fullname,
                    phone = phone,
                    birthday = birthday,
                    gender = gender,
                    address = address,
                    email = email
                )

                Log.d("EDIT_PROFILE_VM", "Server trả về JSON thành công: success = ${response.success}, message = ${response.message}")

                if (response.success && response.user != null) {
                    // 1. Lưu đè dữ liệu mới (bao gồm cả post_count, adopt_count) vào bộ nhớ máy
                    sessionManager.saveUser(response.user)

                    // 2. Cập nhật kết quả chữ để kích hoạt LaunchedEffect hiển thị Toast
                    updateResult = "Cập nhật thành công!"

                    // 3. ĐỢI 1 KHOẢNG NGẮN (1 giây) để người dùng kịp nhìn thấy chữ "Cập nhật thành công!" hiện lên
                    delay(1000)

                    // 4. Sau đó mới chạy hàm quay lại màn hình cũ an toàn
                    Log.d("EDIT_PROFILE_VM", "Kích hoạt callback onSuccess() để popBackStack")
                    onSuccess()
                } else {
                    // Nếu server trả về success = false
                    updateResult = response.message ?: "Cập nhật thất bại từ máy chủ"
                    Log.w("EDIT_PROFILE_VM", "Server từ chối cập nhật công việc: ${response.message}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                updateResult = "Lỗi kết nối mạng: ${e.message}"
                Log.e("EDIT_PROFILE_VM", "Quá trình gọi API bị Crash lỗi mạng: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }
}