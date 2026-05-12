package com.example.project_3.data.remote

import com.example.project_3.data.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {

    @FormUrlEncoded
    @POST("login.php")
    suspend fun login(

        @Field("username") username: String,

        @Field("password") password: String

    ): Response<LoginResponse>
}