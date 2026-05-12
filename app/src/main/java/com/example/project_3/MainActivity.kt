package com.example.project_3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.project_3.ui.auth.LoginScreen
import com.example.project_3.ui.auth.RegisterScreen
import com.example.project_3.ui.home.HomeScreen // Đảm bảo import đúng đường dẫn HomeScreen
import com.example.project_3.ui.theme.Project3Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Project3Theme {
                // Toàn bộ logic điều hướng gom vào một Composable function
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(innerPadding)
        ) {
            // 1. Màn hình Đăng nhập
            composable("login") {
                LoginScreen(
                    onNavigateToRegister = {
                        navController.navigate("register")
                    },
                    onLoginSuccess = {
                        // Chuyển sang Home và xóa lịch sử login để không back lại được
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }

            // 2. Màn hình Đăng ký
            composable("register") {
                RegisterScreen(
                    onNavigateToLogin = {
                        navController.navigate("login")
                    }
                )
            }

            // 3. Màn hình Trang chủ (Mới)
            composable("home") {
                HomeScreen()
            }
        }
    }
}