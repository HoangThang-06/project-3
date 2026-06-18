package com.example.project_3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3.data.model.Pet
import com.example.project_3.data.repository.PetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// 1. SỬA ĐỔI: Chuyển id từ String sang Int để khớp với Backend PHP
data class AdoptApplication(
    val id: Int,
    val applicantName: String,
    val petName: String,
    val petBreed: String,
    val status: String,
    val tags: List<Pair<String, Boolean>>,
    val note: String?,
    val originalPet: Pet? = null // Có thể null vì danh sách request không trả về full object Pet
)

data class AdminAdoptUiState(
    val isLoading: Boolean = false,
    val applications: List<AdoptApplication> = emptyList(),
    val searchText: String = "",
    val errorMessage: String? = null,
    val todayProgress: Float = 0.0f
)

class AdminAdoptViewModel(
    private val petRepository: PetRepository = PetRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminAdoptUiState())
    val uiState: StateFlow<AdminAdoptUiState> = _uiState.asStateFlow()

    private var originalList: List<AdoptApplication> = emptyList()

    init {
        fetchAdoptionRequests()
    }

    // 2. SỬA ĐỔI: Gọi đúng API lấy danh sách yêu cầu nhận nuôi từ Backend
    fun fetchAdoptionRequests() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val response = petRepository.getAllAdoptionRequests()
                if (response.success && response.data != null) {
                    val mappedList = response.data.map { request ->
                        AdoptApplication(
                            id = request.id,
                            applicantName = request.user_name,
                            petName = request.name_pet,
                            petBreed = request.species,
                            status = request.state, // Trạng thái: pending, approved, rejected
                            tags = listOf(
                                "Tuổi: ${request.age}" to false,
                                "Email: ${request.email}" to true
                            ),
                            note = "Ngày đăng ký: ${request.adoption_date}"
                        )
                    }
                    originalList = mappedList

                    // Tính toán tiến độ dựa trên số đơn đã duyệt / tổng số đơn
                    val approvedCount = mappedList.count { it.status == "approved" || it.status == "adopted" }
                    val progress = if (mappedList.isNotEmpty()) approvedCount.toFloat() / mappedList.size else 0f

                    _uiState.update {
                        it.copy(isLoading = false, applications = mappedList, todayProgress = progress)
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = response.message) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Lỗi kết nối: ${e.localizedMessage}") }
            }
        }
    }

    // 3. SỬA ĐỔI: Gọi hàm approveRequest.php (Transaction duyệt đơn hoàn tất)
    fun approveApplication(application: AdoptApplication) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // Gọi API duyệt hoàn tất đơn nhận nuôi
                val response = petRepository.approveAdoptionRequest(application.id)

                if (response.success) {
                    // Sau khi duyệt thành công, tải lại danh sách mới nhất từ Server
                    fetchAdoptionRequests()
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = response.message) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Lỗi: ${e.localizedMessage}") }
            }
        }
    }

    // 4. SỬA ĐỔI: Gọi hàm update_request_state.php với trạng thái là 'rejected'
    fun rejectApplication(applicationId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // Gọi API chuyển trạng thái đơn sang 'rejected'
                val response = petRepository.updateAdoptionRequestState(applicationId, "rejected")

                if (response.success) {
                    // Từ chối thành công, cập nhật lại danh sách tự động
                    fetchAdoptionRequests()
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = response.message) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Lỗi: ${e.localizedMessage}") }
            }
        }
    }

    // XỬ LÝ SEARCH TRÊN UI
    fun onSearchTextChanged(text: String) {
        _uiState.update { it.copy(searchText = text) }
        if (text.isBlank()) {
            _uiState.update { it.copy(applications = originalList) }
        } else {
            val filtered = originalList.filter {
                it.applicantName.contains(text, ignoreCase = true) ||
                        it.petName.contains(text, ignoreCase = true)
            }
            _uiState.update { it.copy(applications = filtered) }
        }
    }
}