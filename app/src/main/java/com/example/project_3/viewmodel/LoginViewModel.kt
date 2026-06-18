package com.example.project_3.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3.data.model.User
import com.example.project_3.data.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val repository = UserRepository()

    var message by mutableStateOf("")
        private set

    var currentUser by mutableStateOf<User?>(null)
        private set

    var navigateTo by mutableStateOf("")
        private set

    fun login(
        username: String,
        password: String
    ) {
        viewModelScope.launch {

            try {

                val response =
                    repository.login(username, password)

                if (response.body()?.success == true) {

                    val user = response.body()?.user

                    currentUser = user

                    message = "Login Success"

                    when(user?.role) {
                        "admin" -> navigateTo = "admin"
                        "user" -> navigateTo = "user"
                    }

                } else {

                    message =
                        response.body()?.message
                            ?: "Login Failed"
                }

            } catch (e: Exception) {

                message = e.message.toString()
            }
        }
    }

    fun clearNavigation() {
        navigateTo = ""
    }
    fun clearMessage() {
        message = ""
    }
}