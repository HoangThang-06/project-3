package com.example.project_3.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3.data.repository.PetRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

class AdminAddPetViewModel : ViewModel() {

    private val repository = PetRepository()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess

    private val _selectedPersonalities = MutableStateFlow<Set<String>>(emptySet())
    val selectedPersonalities: StateFlow<Set<String>> = _selectedPersonalities

    fun togglePersonality(trait: String) {
        val current = _selectedPersonalities.value
        _selectedPersonalities.value = if (current.contains(trait)) {
            current - trait
        } else {
            current + trait
        }
    }

    fun savePet(
        context: Context,
        name: String,
        gender: String, // Đúng với enum('male', 'female')
        description: String,
        ageLabel: String,
        species: String, // Đúng với enum('dog', 'cat', 'other')
        healthStatus: String,
        imageUri: Uri?
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Ánh xạ độ tuổi thành chuỗi số để lưu vào cột int(50) của database
                val ageValue = when (ageLabel) {
                    "Sơ sinh" -> "0"
                    "Trẻ" -> "1"
                    "Trưởng thành" -> "3"
                    "Già" -> "7"
                    else -> "1"
                }

                val finalDescription = buildString {
                    append(description)
                    if (healthStatus.isNotEmpty()) {
                        if (this.isNotEmpty()) append("\n")
                        append("Sức khỏe: $healthStatus")
                    }
                    if (_selectedPersonalities.value.isNotEmpty()) {
                        if (this.isNotEmpty()) append("\n")
                        append("Tính cách: ${_selectedPersonalities.value.joinToString(", ")}")
                    }
                }

                // Xử lý chuyển đổi file ảnh nhị phân gửi lên cột image varchar(200)
                var multipartImage: MultipartBody.Part? = null
                imageUri?.let { uri ->
                    val file = uriToFile(context, uri)
                    if (file != null) {
                        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                        // Lưu ý: Key "image" phải trùng khớp với biến nhận $_FILES['image'] ở PHP
                        multipartImage = MultipartBody.Part.createFormData("image", file.name, requestFile)
                    }
                }

                val response = withContext(Dispatchers.IO) {
                    repository.addPet(
                        namePet = name,
                        gender = gender,
                        description = finalDescription,
                        state = "available", // Đúng với enum('available', 'reserved', 'adopted')
                        imagePart = multipartImage,
                        age = ageValue,
                        species = species
                        // Lưu ý bổ sung: Nếu PetRepository.kt của bạn đã cập nhật nhận thêm tham số click,
                        // bạn hãy bổ sung truyền biến: click = "0" vào đây.
                    )
                }

                if (response.success) {
                    _isSuccess.value = true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun uriToFile(context: Context, uri: Uri): File? {
        try {
            val contentResolver = context.contentResolver
            val filePath = context.cacheDir.toString() + File.separator + "upload_pet_image.jpg"
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

    fun resetSuccess() {
        _isSuccess.value = false
    }
}