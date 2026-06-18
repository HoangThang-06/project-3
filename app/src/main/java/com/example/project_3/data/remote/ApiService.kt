package com.example.project_3.data.remote

import com.example.project_3.data.model.AddCommentResponse
import com.example.project_3.data.model.ArticleResponse
import com.example.project_3.data.model.LoginResponse
import com.example.project_3.data.model.PetResponse
import com.example.project_3.data.model.RegisterResponse
import com.example.project_3.data.model.UserResponse
import com.example.project_3.data.model.PetDetailResponse
import com.example.project_3.data.model.PostHistoryResponse
import com.example.project_3.data.model.BaseResponse
import com.example.project_3.data.model.CommentResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
//import com.example.project_3.data.model.AdoptHistoryResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {

    // ==========================================
    // 1. AUTH APIs (Xác thực tài khoản)
    // ==========================================

    @FormUrlEncoded
    @POST("login.php")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    @FormUrlEncoded
    @POST("register.php")
    suspend fun register(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("email") email: String
    ): Response<RegisterResponse>


    // ==========================================
    // 2. PET APIs (Quản lý thú cưng)
    // ==========================================

    @GET("pet/get_all_pets.php")
    suspend fun getAllPets(
        @Query("id_user") idUser: Int
    ): PetResponse

    @GET("pet/get_pet.php")
    suspend fun getPet(
        @Query("id_pet") idPet: String
    ): PetResponse

    @GET("pet/get_top_pet.php")
    suspend fun getTopPet(): PetResponse

    @GET("pet/get_pet_detail.php")
    suspend fun getPetDetail(
        @Query("id_pet") idPet: Int
    ): PetDetailResponse

    @Multipart
    @POST("pet/add_pet.php") // Nhớ chỉnh sửa lại endpoint cho đúng với cấu trúc server của bạn
    suspend fun addPet(
        @Part("name_pet") namePet: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part("description") description: RequestBody,
        @Part("state") state: RequestBody,
        @Part image: MultipartBody.Part?, // Đối với file nhị phân của ảnh
        @Part("age") age: RequestBody,
        @Part("species") species: RequestBody
    ): PetResponse

    @FormUrlEncoded
    @POST("pet/update_pet.php")
    suspend fun updatePet(
        @Field("id_pet") idPet: String,
        @Field("name_pet") namePet: String,
        @Field("gender") gender: String,
        @Field("description") description: String,
        @Field("state") state: String,
        @Field("image") image: String,
        @Field("age") age: String,
        @Field("species") species: String
    ): PetResponse

    @FormUrlEncoded
    @POST("pet/delete_pet.php")
    suspend fun deletePet(
        @Field("id_pet") idPet: String
    ): PetResponse


    // ==========================================
    // 3. USER & PROFILE APIs (Hồ sơ người dùng)
    // ==========================================

    @GET("user/get_user.php")
    suspend fun getUser(
        @Query("id_user") idUser: String
    ): UserResponse

    @GET("user/get_all_users.php")
    suspend fun getAllUsers(
        @Query("current_user_id") currentUserId: String
    ): UserResponse

    @FormUrlEncoded
    @POST("user/delete_user.php")
    suspend fun deleteUser(
        @Field("id_user") idUser: String
    ): UserResponse

    @FormUrlEncoded
    @POST("user/update_user.php")
    suspend fun updateUser(
        @Field("id_user") idUser: String,
        @Field("username") username: String,
        @Field("fullname") fullname: String,
        @Field("phone") phone: String,
        @Field("birthday") birthday: String,
        @Field("gender") gender: String,
        @Field("address") address: String,
        @Field("avatar") avatar: String,
        @Field("email") email: String,
        @Field("status") status: String
    ): UserResponse

    @FormUrlEncoded
    @POST("user/update_profile.php")
    suspend fun updateProfile(
        @Field("id_user") idUser: Int,
        @Field("fullname") fullname: String,
        @Field("phone") phone: String,
        @Field("birthday") birthday: String,
        @Field("gender") gender: String,
        @Field("address") address: String,
        @Field("email") email: String
    ): UserResponse

    @FormUrlEncoded
    @POST("user/reset_password.php")
    suspend fun resetPassword(
        @Field("email") email: String,
        @Field("new_password") newPassword: String
    ): UserResponse


    // ==========================================
    // 4. SOCIAL & ARTICLE APIs (Mạng xã hội công đồng)
    // ==========================================

    @GET("get_articles.php")
    suspend fun getArticles(
        @Query("user_id") userId: Int
    ): ArticleResponse

    @FormUrlEncoded
    @POST("toggle_like_article.php")
    suspend fun toggleLikeArticle(
        @Field("user_id") userId: Int,
        @Field("article_id") articleId: Int
    ): BaseResponse


    // ==========================================
    // 5. USER HISTORY & ACTIONS APIs (Hoạt động cá nhân)
    // ==========================================

    @GET("get_adopt_history.php")
    suspend fun getAdoptHistory(
        @Query("id_user") idUser: Int
    ): AdoptHistoryResponse

    @GET("get_post_history.php")
    suspend fun getPostHistory(
        @Query("id_user") idUser: Int
    ): PostHistoryResponse

    @FormUrlEncoded
    @POST("update_post_status.php")
    suspend fun updatePostStatus(
        @Field("id_article") idArticle: Int,
        @Field("action") action: String
    ): BaseResponse

    @FormUrlEncoded
    @POST("add_follow_pet.php")
    suspend fun addFollowPet(
        @Field("id_user") idUser: Int,
        @Field("id_pet") idPet: Int
    ): BaseResponse

    @GET("get_favorite_pets.php")
    suspend fun getFavoritePets(
        @Query("id_user") idUser: Int
    ): PetResponse

    @GET("get_comments.php")
    suspend fun getComments(
        @Query("article_id") articleId: Int,
        @Query("page") page: Int
    ): CommentResponse

    // Khai báo chuẩn trong ApiService.kt
    @FormUrlEncoded
    @POST("add_comment.php")
    suspend fun addComment(
        @Field("article_id") articleId: Int,
        @Field("user_id") userId: Int,
        @Field("content") content: String
    ): AddCommentResponse

    @Multipart
    @POST("add_article.php")
    suspend fun addArticle(
        @Part("user_id") userId: RequestBody,
        @Part("content") content: RequestBody,
        @Part("category") category: RequestBody,
        @Part image: MultipartBody.Part?
    ): BaseResponse

    @Multipart
    @POST("add_report.php")
    suspend fun addReport(
        @Part("user_id") userId: RequestBody,
        @Part("description") description: RequestBody,
        @Part("status") status: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part("address") address: RequestBody,
        @Part image: MultipartBody.Part?
    ): BaseResponse
}