package com.example.project_3.data.repository

import com.example.project_3.data.remote.RetrofitClient // Đảm bảo đúng đường dẫn tới RetrofitClient của bạn
import com.example.project_3.data.model.BaseResponse // Class chứa success và message
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class ReportRepository {

    private val apiService = RetrofitClient.api

    suspend fun addReport(
        userId: Int,
        description: String,
        status: String, // 1. ĐÃ THÊM: Nhận tham số status từ ViewModel truyền sang
        latitude: Double,
        longitude: Double,
        address: String,
        imageBytes: ByteArray?,
        imageFileName: String?
    ): BaseResponse {

        // 2. Chuyển đổi dữ liệu thô sang RequestBody chuẩn cấu trúc text/plain
        val userIdBody = userId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val descBody = description.toRequestBody("text/plain".toMediaTypeOrNull())
        val statusBody = status.toRequestBody("text/plain".toMediaTypeOrNull()) // 3. ĐÃ THÊM: Ép kiểu status sang RequestBody
        val latBody = latitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val lngBody = longitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val addrBody = address.toRequestBody("text/plain".toMediaTypeOrNull())

        // Đóng gói dữ liệu mảng bytes của file ảnh sang định dạng MultipartBody.Part
        var imagePart: MultipartBody.Part? = null
        if (imageBytes != null && imageFileName != null) {
            val requestFile = imageBytes.toRequestBody("image/*".toMediaTypeOrNull())
            imagePart = MultipartBody.Part.createFormData("image", imageFileName, requestFile)
        }

        // 4. ĐÃ SỬA: Truyền thêm tham số status vào hàm của apiService
        return apiService.addReport(
            userId = userIdBody,
            description = descBody,
            status = statusBody, // <-- Gửi key "status" lên Server PHP
            latitude = latBody,
            longitude = lngBody,
            address = addrBody,
            image = imagePart
        )
    }
}