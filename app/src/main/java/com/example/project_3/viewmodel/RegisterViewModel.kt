package com.example.project_3.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_3.data.repository.UserRepository
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private val repository = UserRepository()

    var message by mutableStateOf("")
        private set

    fun register(
        username: String,
        password: String,
        email: String
    ){

        viewModelScope.launch {

            try {

                val response =
                    repository.register(
                        username,
                        password,
                        email
                    )

                if(
                    response.isSuccessful &&
                    response.body()?.success == true
                ){

                    message =
                        response.body()?.message
                            ?: "Register success"

                }else{

                    message =
                        response.body()?.message
                            ?: "Register failed"
                }

            }catch (e: Exception){

                message = e.message.toString()
            }
        }
    }
}