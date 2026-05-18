package com.example.project_3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType // THÊM IMPORT NÀY
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument // THÊM IMPORT NÀY
import com.example.project_3.ui.auth.LoginScreen
import com.example.project_3.ui.auth.RegisterScreen
import com.example.project_3.ui.home.HomeScreen
import com.example.project_3.ui.pet.PetDetailScreen // THÊM IMPORT MÀN HÌNH CHI TIẾT CỦA BẠN
import com.example.project_3.ui.theme.Project3Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Bật tính năng hiển thị sát rìa màn hình
        setContent {
            Project3Theme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // BỎ Scaffold bọc ngoài ở đây để các màn hình con tự quản lý innerPadding của riêng chúng.
    // Điều này giúp PetDetailScreen có thể đẩy ảnh tràn lên đỉnh thanh trạng thái (Status Bar).
    NavHost(
        navController = navController,
        startDestination = "login",
        modifier = Modifier.fillMaxSize()
    ) {
        // 1. Màn hình Đăng nhập
        composable("login") {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate("register")
                },
                onLoginSuccess = {
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

        // 3. Màn hình Trang chủ
        // Bên trong NavHost của MainActivity.kt
        composable("home") {
            // Truyền cái navController chính của hệ thống vào đây là hết crash hoàn toàn!
            HomeScreen(mainNavController = navController)
        }

        // 4. ĐĂNG KÝ TUYẾN ĐƯỜNG CHI TIẾT THÚ CƯNG (THÊM MỚI TẠI ĐÂY)
        // Tuyến đường nhận tham số có dạng: pet_detail/1, pet_detail/2,...
        composable(
            route = "pet_detail/{petId}",
            arguments = listOf(
                navArgument("petId") { type = NavType.IntType } // Định nghĩa tham số truyền đi bắt buộc là kiểu Số nguyên (Int)
            )
        ) { backStackEntry ->
            // Bóc tách lấy ID an toàn từ argument, nếu không tìm thấy mặc định lấy ID số 0
            val petId = backStackEntry.arguments?.getInt("petId") ?: 0

            // Gọi màn hình chi tiết, truyền ID vừa bóc tách và cây điều hướng navController vào
            PetDetailScreen(idPet = petId, navController = navController)
        }
    }
}