package com.example.project_3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.project_3.ui.adopt.FavoriteScreen // <-- THÊM IMPORT NÀY
import com.example.project_3.ui.auth.LoginScreen
import com.example.project_3.ui.auth.RegisterScreen
import com.example.project_3.ui.home.HomeScreen
import com.example.project_3.ui.adopt.PetDetailScreen
import com.example.project_3.ui.profile.AdoptHistoryScreen
import com.example.project_3.ui.profile.EditProfileScreen
import com.example.project_3.ui.profile.PostHistoryScreen
import com.example.project_3.ui.theme.Project3Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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

        // 3. Màn hình Trang chủ (Chứa BottomBar dẫn đến ProfileScreen)
        composable("home") {
            HomeScreen(mainNavController = navController)
        }

        // 4. Màn hình Chi tiết Thú cưng
        composable(
            route = "pet_detail/{petId}",
            arguments = listOf(
                navArgument("petId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val petId = backStackEntry.arguments?.getInt("petId") ?: 0
            PetDetailScreen(idPet = petId, navController = navController)
        }

        // 5. Màn hình Chỉnh sửa thông tin cá nhân
        composable("edit_profile") {
            EditProfileScreen(navController = navController)
        }

        // 6. Màn hình Lịch sử nhận nuôi
        composable("adopt_history") {
            AdoptHistoryScreen(navController = navController)
        }

        // 7. Màn hình Lịch sử bài viết của tôi
        composable("post_history") {
            PostHistoryScreen(navController = navController)
        }

        // ====================================================
        // THÊM MỚI TẠI ĐÂY: 8. Màn hình Thú cưng đang theo dõi
        // ====================================================
        composable("favorite_pets") {
            FavoriteScreen(navController = navController)
        }
    }
}