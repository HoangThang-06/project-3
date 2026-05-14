package com.example.project_3.data.remote

import com.example.project_3.data.model.LoginResponse
import com.example.project_3.data.model.PetResponse
import com.example.project_3.data.model.RegisterResponse
import com.example.project_3.data.model.UserResponse

import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    // =========================
    // AUTH APIs
    // =========================

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

    // =========================
    // PET APIs
    // =========================

    @GET("pet/get_all_pets.php")
    suspend fun getAllPets(): PetResponse

    @GET("pet/get_pet.php")
    suspend fun getPet(

        @Query("id_pet")
        idPet: String

    ): PetResponse

    @GET("pet/get_top_pet.php")
    suspend fun getTopPet(): PetResponse

    @FormUrlEncoded
    @POST("pet/add_pet.php")
    suspend fun addPet(

        @Field("name_pet")
        namePet: String,

        @Field("gender")
        gender: String,

        @Field("description")
        description: String,

        @Field("state")
        state: String,

        @Field("image")
        image: String,

        @Field("age")
        age: String,

        @Field("species")
        species: String

    ): PetResponse

    @FormUrlEncoded
    @POST("pet/update_pet.php")
    suspend fun updatePet(

        @Field("id_pet")
        idPet: String,

        @Field("name_pet")
        namePet: String,

        @Field("gender")
        gender: String,

        @Field("description")
        description: String,

        @Field("state")
        state: String,

        @Field("image")
        image: String,

        @Field("age")
        age: String,

        @Field("species")
        species: String

    ): PetResponse

    @FormUrlEncoded
    @POST("pet/delete_pet.php")
    suspend fun deletePet(

        @Field("id_pet")
        idPet: String

    ): PetResponse

    // =========================
    // USER APIs
    // =========================

    @GET("user/get_user.php")
    suspend fun getUser(

        @Query("id_user")
        idUser: String

    ): UserResponse

    @GET("user/get_all_users.php")
    suspend fun getAllUsers(

        @Query("current_user_id")
        currentUserId: String

    ): UserResponse

    @FormUrlEncoded
    @POST("user/delete_user.php")
    suspend fun deleteUser(

        @Field("id_user")
        idUser: String

    ): UserResponse

    @FormUrlEncoded
    @POST("user/update_user.php")
    suspend fun updateUser(

        @Field("id_user")
        idUser: String,

        @Field("username")
        username: String,

        @Field("fullname")
        fullname: String,

        @Field("phone")
        phone: String,

        @Field("birthday")
        birthday: String,

        @Field("gender")
        gender: String,

        @Field("address")
        address: String,

        @Field("avatar")
        avatar: String,

        @Field("email")
        email: String,

        @Field("status")
        status: String

    ): UserResponse

    @FormUrlEncoded
    @POST("user/reset_password.php")
    suspend fun resetPassword(

        @Field("email")
        email: String,

        @Field("new_password")
        newPassword: String

    ): UserResponse
    @GET("get_articles.php")
    suspend fun getAllArticles(): com.example.project_3.data.model.ArticleResponse

    // Like bài viết: Truyền userId lấy từ SessionManager.getUserId()
    @FormUrlEncoded
    @POST("like_article.php")
    suspend fun likeArticle(
        @Field("user_id") userId: Int,
        @Field("article_id") articleId: Int
    ): com.example.project_3.data.model.SimpleResponse
}