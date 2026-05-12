package com.example.project_3.data.remote

import com.example.project_3.data.model.LoginResponse
import com.example.project_3.data.model.RegisterResponse
import com.example.project_3.data.model.PetResponse

import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.GET

interface ApiService {

    @FormUrlEncoded
    @POST("login.php")
    suspend fun login(
        @Field("username")
        username: String,

        @Field("password")
        password: String

    ): Response<LoginResponse>

    @FormUrlEncoded
    @POST("register.php")
    suspend fun register(

        @Field("username")
        username: String,

        @Field("password")
        password: String,

        @Field("email")
        email: String

    ): Response<RegisterResponse>

    @GET("pet/get_all_pets.php")
    suspend fun getAllPets(): PetResponse
}