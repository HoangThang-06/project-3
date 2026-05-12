package com.example.project_3.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.project_3.data.repository.UserRepository

class LoginViewModel : ViewModel() {

    private val repository = UserRepository()

    var message by mutableStateOf("")
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

                    message = "Login Success"

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
    fun clearMessage() {
        message = ""
    }
}