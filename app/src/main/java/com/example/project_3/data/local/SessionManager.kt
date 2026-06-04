package com.example.project_3.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.project_3.data.model.User
import com.google.gson.Gson // Lưu ý: Cần import Gson

class SessionManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("session", Context.MODE_PRIVATE)

    private val gson = Gson()

    // --- CÁC HÀM CŨ CỦA BẠN (GIỮ NGUYÊN ĐỂ KHÔNG LỖI LOG/REG) ---
    fun saveUserId(id: Int) {
        sharedPreferences.edit()
            .putInt("user_id", id)
            .apply()
    }

    fun getUserId(): Int {
        return sharedPreferences.getInt("user_id", -1)
    }

    fun logout() {
        sharedPreferences.edit()
            .clear()
            .apply()
    }

    // --- THÊM 2 HÀM MỚI NÀY ĐỂ FIX LỖI CHO PROFILE VIEWMODEL ---

    // 1. Hàm lưu toàn bộ Object User (Giải quyết lỗi Unresolved reference 'saveUser')
    fun saveUser(user: User) {
        val userJson = gson.toJson(user) // Chuyển đối tượng User thành chuỗi String JSON
        sharedPreferences.edit()
            .putString("user_json", userJson)
            .putInt("user_id", user.id_user) // Tiện tay cập nhật luôn cả user_id số nguyên của bạn
            .apply()
    }

    // 2. Hàm lấy toàn bộ Object User (Giải quyết lỗi Unresolved reference 'getUser' và Ép kiểu)
    fun getUser(): User? {
        val userJson = sharedPreferences.getString("user_json", null) ?: return null
        return try {
            gson.fromJson(userJson, User::class.java) // Chuyển chuỗi JSON ngược thành Object User
        } catch (e: Exception) {
            null
        }
    }
}