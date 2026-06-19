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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project_3.ui.profile.FavoriteScreen
import com.example.project_3.ui.auth.LoginScreen
import com.example.project_3.ui.auth.RegisterScreen
import com.example.project_3.ui.home.HomeScreen
import com.example.project_3.ui.adopt.PetDetailScreen
import com.example.project_3.ui.profile.AdoptHistoryScreen
import com.example.project_3.ui.profile.EditProfileScreen
import com.example.project_3.ui.profile.PostHistoryScreen
import com.example.project_3.ui.theme.Project3Theme
import com.example.project_3.ui.admin.*
import com.example.project_3.viewmodel.AdminManagePetViewModel
import com.example.project_3.viewmodel.AdminManageArticleViewModel
import com.example.project_3.viewmodel.AdminProfileViewModel
import com.example.project_3.viewmodel.AdminManageUserViewModel
import com.example.project_3.ui.admin.AdminManageUserScreen
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

    // Khởi tạo các ViewModel dùng chung ở tầng Navigation để chia sẻ dữ liệu giữa các màn hình quản lý
    val adminManagePetViewModel: AdminManagePetViewModel = viewModel()
    val adminManageArticleViewModel: AdminManageArticleViewModel = viewModel()
    val adminProfileViewModel: AdminProfileViewModel = viewModel()
    val adminManageUserViewModel: AdminManageUserViewModel = viewModel()
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
                onAdminLoginSuccess = {
                    navController.navigate("admin_home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onUserLoginSuccess = {
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

        // 3. Màn hình Trang chủ User
        composable("home") {
            HomeScreen(mainNavController = navController)
        }

        // ====================================================
        // HỆ THỐNG ROUTE DÀNH CHO ADMIN
        // ====================================================

        // Màn hình chính Dashboard của Admin
        composable("admin_home") {
            AdminDashboard(navController = navController)
        }

        // Màn hình quản lý thú cưng
        composable("admin_manage_pet") {
            AdminManagePet(navController, adminManagePetViewModel)
        }

        // Màn hình quản lý chi tiết thú cưng (Sửa/Xóa)
        composable("admin_pet_detail") {
            val selectedPet = adminManagePetViewModel.selectedPet
            if (selectedPet != null) {
                AdminPetDetailScreen(
                    pet = selectedPet,
                    navController = navController,
                    viewModel = adminManagePetViewModel
                )
            }
        }

        composable("admin_add_pet") {
            AdminAddPet(navController = navController)
        }

        // Màn hình duyệt đơn nhận nuôi
        composable("admin_adopt") {
            AdminAdopt(navController = navController)
        }

        // --- CẬP NHẬT ROUTE CỘNG ĐỒNG SOCIAL CHO ADMIN ---
        composable("admin_social") {
            AdminSocial(
                navController = navController,
                viewModel = adminManageArticleViewModel,
                onEditArticleClick = { article ->
                    // Code điều hướng sửa bài viết của bạn tại đây...
                },
                onAddArticleClick = {
                    // Code điều hướng thêm bài viết của bạn tại đây...
                }
            )
        }

        // Màn hình sửa đổi thông tin chi tiết bài viết/bài báo của Admin
        composable("admin_article_detail") {
            val currentArticle = adminManageArticleViewModel.selectedArticle

            if (currentArticle != null) {
                AdminArticleDetailScreen(
                    article = currentArticle,
                    navController = navController,
                    viewModel = adminManageArticleViewModel
                )
            } else {
                navController.popBackStack()
            }
        }

        composable(
            route = "admin_profile/{adminId}",
            arguments = listOf(
                navArgument("adminId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val adminId = backStackEntry.arguments?.getString("adminId") ?: ""

            AdminProfileScreen(
                currentAdminId = adminId,
                navController = navController,
                viewModel = adminProfileViewModel // Lúc này nó sẽ lấy từ biến dùng chung ở đầu hàm
            )
        }

        composable(
            route = "admin_edit_profile/{adminId}",
            arguments = listOf(
                navArgument("adminId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val adminId = backStackEntry.arguments?.getString("adminId") ?: ""
            EditAdminProfileScreen(
                adminId = adminId,
                navController = navController,
                viewModel = adminProfileViewModel
            )
        }

        // Đổi từ adminId = adminId thành currentUserId = adminId để khớp định nghĩa hàm
        // Thay đổi route thành dạng tham số tùy chọn (dùng dấu chấm hỏi ?adminId={adminId})
        composable(route = "admin_manage_user") {
            AdminManageUserScreen(
                currentUserId = "", // Truyền chuỗi rỗng thoải mái vì bên trong Screen đã tự lấy từ Session
                navController = navController,
                viewModel = adminManageUserViewModel
            )
        }

        // ====================================================
        // CÁC MÀN HÌNH CHỨC NĂNG KHÁC CỦA USER
        // ====================================================

        // Màn hình Chi tiết Thú cưng (Giao diện phía User xem)
        composable(
            route = "pet_detail/{petId}",
            arguments = listOf(
                navArgument("petId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val petId = backStackEntry.arguments?.getInt("petId") ?: 0
            PetDetailScreen(idPet = petId, navController = navController)
        }

        // Màn hình Chỉnh sửa thông tin cá nhân
        composable("edit_profile") {
            EditProfileScreen(navController = navController)
        }

        // Màn hình Lịch sử nhận nuôi
        composable("adopt_history") {
            AdoptHistoryScreen(navController = navController)
        }

        // Màn hình Lịch sử bài viết cá nhân
        composable("post_history") {
            PostHistoryScreen(navController = navController)
        }

        // Màn hình Thú cưng đang theo dõi
        composable("favorite_pets") {
            FavoriteScreen(navController = navController)
        }
    }
}