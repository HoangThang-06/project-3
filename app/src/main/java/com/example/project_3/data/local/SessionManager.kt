package com.example.project_3.data.local

import android.content.Context

class SessionManager(context: Context) {

    private val sharedPreferences =
        context.getSharedPreferences(
            "session",
            Context.MODE_PRIVATE
        )

    fun saveUserId(id: Int) {

        sharedPreferences.edit()
            .putInt("user_id", id)
            .apply()
    }

    fun getUserId(): Int {

        return sharedPreferences
            .getInt("user_id", -1)
    }

    fun logout() {

        sharedPreferences.edit()
            .clear()
            .apply()
    }
}