package com.example.project_3.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3.data.model.AdminActivity
import com.example.project_3.data.model.AdminStatsResponse
import com.example.project_3.data.model.GenericResponse
import com.example.project_3.data.remote.RetrofitClient
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class AdminDashboardViewModel : ViewModel() {
    var totalPets by mutableStateOf("0")
    var totalAdoptions by mutableStateOf("0")
    var activeRescues by mutableStateOf("0")
    var recentActivities by mutableStateOf<List<AdminActivity>>(emptyList())

    var isLoading by mutableStateOf(false)

    // 📅 State để quản lý việc nhập dữ liệu cho bảng events
    var eventTitle by mutableStateOf("")
    var eventDate by mutableStateOf("")
    var eventLocation by mutableStateOf("")
    var eventImageUrl by mutableStateOf("")

    fun fetchDashboardData() {
        viewModelScope.launch {
            isLoading = true
            try {
                val response: AdminStatsResponse = RetrofitClient.api.getAdminStats()

                if (response.success) {
                    // Cập nhật chuỗi trực tiếp để kích hoạt giao diện vẽ lại
                    totalPets = "${response.total_pets}"
                    totalAdoptions = "${response.total_adoptions}"
                    activeRescues = "${response.active_rescues}"

                    // Ép kiểu sang ArrayList để Compose nhận biết danh sách có thay đổi
                    recentActivities = ArrayList(response.recent_activities)
                } else {
                    totalPets = "False"
                    totalAdoptions = "False"
                    activeRescues = "False"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // 🔥 ÉP LỖI HIỂN THỊ THẲNG LÊN UI ĐỂ BẮT BỆNH
                totalPets = "Lỗi: ${e.javaClass.simpleName}"
                totalAdoptions = e.localizedMessage ?: "Unknown Error"
                activeRescues = "Vào Catch ngầm"
                recentActivities = emptyList()
            } finally {
                isLoading = false
            }
        }
    }

    // 🚀 Giữ nguyên hàm addEvent của bạn (Không thay đổi logic upload hiện tại)
    fun addEvent(context: Context, onComplete: (Boolean, String) -> Unit) {
        if (eventTitle.trim().isEmpty() || eventDate.trim().isEmpty() || eventLocation.trim().isEmpty()) {
            onComplete(false, "Vui lòng nhập đủ thông tin Event!")
            return
        }

        viewModelScope.launch {
            isLoading = true
            try {
                val titleBody = eventTitle.toRequestBody("text/plain".toMediaTypeOrNull())
                val dateBody = eventDate.toRequestBody("text/plain".toMediaTypeOrNull())
                val locationBody = eventLocation.toRequestBody("text/plain".toMediaTypeOrNull())

                var imagePart: MultipartBody.Part? = null
                if (eventImageUrl.isNotEmpty()) {
                    val uri = Uri.parse(eventImageUrl)
                    val file = uriToFile(context, uri)
                    if (file != null) {
                        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                        imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)
                    }
                }

                val response: GenericResponse = RetrofitClient.api.addEvent(
                    title = titleBody,
                    date = dateBody,
                    location = locationBody,
                    image = imagePart
                )

                if (response.success) {
                    eventTitle = ""
                    eventDate = ""
                    eventLocation = ""
                    eventImageUrl = ""
                    onComplete(true, response.message)

                    // Tự động làm mới lại số liệu thống kê sau khi thêm thành công
                    fetchDashboardData()
                } else {
                    onComplete(false, response.message)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onComplete(false, "Lỗi kết nối mạng: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    private fun uriToFile(context: Context, uri: Uri): File? {
        try {
            val contentResolver = context.contentResolver
            val filePath = context.cacheDir.toString() + File.separator + "temp_event_${System.currentTimeMillis()}.jpg"
            val file = File(filePath)
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val outputStream = FileOutputStream(file)
            val buf = ByteArray(1024)
            var len: Int
            while (inputStream.read(buf).also { len = it } > 0) {
                outputStream.write(buf, 0, len)
            }
            outputStream.close()
            inputStream.close()
            return file
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}