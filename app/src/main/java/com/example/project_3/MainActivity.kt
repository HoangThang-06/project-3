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
import com.example.project_3.ui.theme.Project3Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Project3Theme {
                // Khởi tạo NavController để quản lý chuyển màn hình
                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding -> // Nhận biến padding từ Scaffold

                    // Sử dụng NavHost để định nghĩa các màn hình trong App
                    NavHost(
                        navController = navController,
                        startDestination = "login", // Màn hình mặc định là Login
                        modifier = Modifier.padding(innerPadding) // Sử dụng innerPadding tại đây để hết lỗi
                    ) {
                        // Khai báo màn hình Đăng nhập
                        composable("login") {
                            LoginScreen(
                                onNavigateToRegister = {
                                    navController.navigate("register")
                                }
                            )
                        }

                        // Khai báo màn hình Đăng ký
                        composable("register") {
                            RegisterScreen(
                                onNavigateToLogin = {
                                    navController.navigate("login")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}