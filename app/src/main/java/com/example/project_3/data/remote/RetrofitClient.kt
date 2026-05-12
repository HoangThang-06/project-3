package com.example.project_3.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL =
        "http://10.0.2.2/project-3/"

    private val retrofit by lazy {

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
    }

    // API chính
    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    // Pet API
    val petApi: PetApi by lazy {
        retrofit.create(PetApi::class.java)
    }
}