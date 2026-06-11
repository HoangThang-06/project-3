package com.example.project_3.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3.data.repository.ReportRepository
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class ReportViewModel : ViewModel() {

    private val repository = ReportRepository()

    // Quản lý trạng thái hiển thị trên giao diện (UI State)
    var isSubmitting = mutableStateOf(false)
    var currentAddress = mutableStateOf("Đang xác định vị trí...")
    var latitude = mutableStateOf(0.0)
    var longitude = mutableStateOf(0.0)

    // Hàm tự động lấy tọa độ GPS thực tế của điện thoại
    @SuppressLint("MissingPermission")
    fun fetchCurrentLocation(context: Context) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        currentAddress.value = "Đang quét GPS..."

        // Lấy vị trí hiện tại với độ chính xác cao nhất
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                if (location != null) {
                    latitude.value = location.latitude
                    longitude.value = location.longitude

                    // Chạy Coroutine độc lập để đẩy tác vụ dịch địa chỉ (Geocoding) xuống luồng ngầm
                    viewModelScope.launch(Dispatchers.IO) {
                        try {
                            val geocoder = Geocoder(context, Locale.getDefault())
                            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)

                            // Trở lại luồng chính (Main Thread) để cập nhật giao diện Compose an toàn
                            withContext(Dispatchers.Main) {
                                if (!addresses.isNullOrEmpty()) {
                                    currentAddress.value = addresses[0].getAddressLine(0) ?: "Không xác định được tên đường"
                                } else {
                                    currentAddress.value = "Tọa độ: ${location.latitude}, ${location.longitude}"
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            // Nếu lỗi Geocoder (do máy ảo thiếu Google Play Services hoặc mất mạng), chuyển về tọa độ thô
                            withContext(Dispatchers.Main) {
                                currentAddress.value = "Tọa độ: ${location.latitude}, ${location.longitude}"
                            }
                        }
                    }
                } else {
                    currentAddress.value = "Không thể lấy GPS. Vui lòng bật định vị!"
                }
            }
            .addOnFailureListener {
                currentAddress.value = "Lỗi khi lấy vị trí: ${it.message}"
            }
    }

    // Hàm gửi báo cáo cứu trợ lên MySQL
    fun submitReport(
        userId: Int,
        description: String,
        status: String, // Nhận trạng thái động từ Screen sang
        imageBytes: ByteArray?,
        imageFileName: String?,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        if (description.trim().isEmpty()) {
            onFailure("Vui lòng nhập mô tả tình trạng thú cưng")
            return
        }

        viewModelScope.launch(Dispatchers.IO) { // Chạy ngầm tác vụ mạng gửi lên server PHP
            try {
                withContext(Dispatchers.Main) { isSubmitting.value = true }

                // FIX CHÍNH: Thay 'statusBody' không tồn tại bằng biến 'status' của hàm nhận vào
                val response = repository.addReport(
                    userId = userId,
                    description = description,
                    status = status, // <-- ĐÃ SỬA TẠI ĐÂY
                    latitude = latitude.value,
                    longitude = longitude.value,
                    address = currentAddress.value,
                    imageBytes = imageBytes,
                    imageFileName = imageFileName
                )

                withContext(Dispatchers.Main) {
                    if (response.success) {
                        onSuccess()
                    } else {
                        onFailure(response.message ?: "Gửi báo cáo thất bại!")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    onFailure("Lỗi kết nối mạng: ${e.message}")
                }
            } finally {
                withContext(Dispatchers.Main) { isSubmitting.value = false }
            }
        }
    }
}