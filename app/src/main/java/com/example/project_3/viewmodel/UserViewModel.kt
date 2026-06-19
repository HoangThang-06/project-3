package com.example.project_3.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3.data.model.User
import com.example.project_3.data.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    private val repository = UserRepository()

    // =========================
    // USER DATA
    // =========================

    var user = mutableStateOf<User?>(null)
        private set

    var userList = mutableStateListOf<User>()
        private set

    // =========================
    // STATE
    // =========================

    var isLoading = mutableStateOf(false)
        private set

    var message = mutableStateOf("")
        private set

    // =========================
    // GET USER BY ID
    // =========================

    fun getUser(idUser: String) {

        viewModelScope.launch {

            try {

                isLoading.value = true

                val response =
                    repository.getUser(idUser)

                if (response.success) {

                    user.value = response.user

                } else {

                    message.value =
                        response.message ?: "Get user failed"
                }

            } catch (e: Exception) {

                message.value =
                    e.message ?: "Network error"

            } finally {

                isLoading.value = false
            }
        }
    }

    // =========================
    // GET ALL USERS
    // =========================

    fun getAllUsers(currentUserId: String) {

        viewModelScope.launch {

            try {

                isLoading.value = true

                val response =
                    repository.getAllUsers(currentUserId)

                if (response.success) {

                    userList.clear()

                    response.users?.let {
                        userList.addAll(it)
                    }

                } else {

                    message.value =
                        response.message ?: "No users found"
                }

            } catch (e: Exception) {

                message.value =
                    e.message ?: "Network error"

            } finally {

                isLoading.value = false
            }
        }
    }

    // =========================
    // DELETE USER
    // =========================

    fun deleteUser(idUser: String) {

        viewModelScope.launch {

            try {

                val response =
                    repository.deleteUser(idUser)

                message.value =
                    response.message ?: ""

                if (response.success) {

                    userList.removeAll {
                        it.id_user.toString() == idUser
                    }
                }

            } catch (e: Exception) {

                message.value =
                    e.message ?: "Delete failed"
            }
        }
    }

    // =========================
    // RESET PASSWORD
    // =========================

    fun resetPassword(
        email: String,
        newPassword: String
    ) {

        viewModelScope.launch {

            try {

                isLoading.value = true

                val response =
                    repository.resetPassword(
                        email,
                        newPassword
                    )

                message.value =
                    response.message ?: ""

            } catch (e: Exception) {

                message.value =
                    e.message ?: "Reset password failed"

            } finally {

                isLoading.value = false
            }
        }
    }

    // =========================
    // CLEAR MESSAGE
    // =========================

    fun clearMessage() {

        message.value = ""
    }
}