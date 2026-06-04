package com.example.project_3.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect // THÊM IMPORT NÀY
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.project_3.data.local.SessionManager
import com.example.project_3.viewmodel.ProfileViewModel
import com.example.project_3.viewmodel.factory.ProfileViewModelFactory

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory(SessionManager(LocalContext.current)))
) {
    // Đọc trạng thái dữ liệu người dùng và trạng thái loading từ ViewModel
    val user = viewModel.user
    val isLoading = viewModel.isLoading

    val context = LocalContext.current
    val baseUrl = "http://10.0.2.2/project-3/upload"

    // =========================================================================
    // ĐOẠN SỬA ĐỔI CHÍNH: Lắng nghe sự kiện quay lại màn hình từ Navigation Stack
    // Mỗi khi màn hình Profile hiển thị lại, lệnh này bắt ViewModel nạp lại dữ liệu
    // =========================================================================
    LaunchedEffect(navController.currentBackStackEntry) {
        viewModel.refreshUser()
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFFBFBFB))) {
        if (isLoading && user == null) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color(0xFF8D4000)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // --- 1. THÔNG TIN CÁ NHÂN ---
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = "$baseUrl${user?.avatar ?: "/avatars/default.png"}",
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = user?.fullname ?: "Người dùng",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Tên tài khoản: ${user?.username ?: ""}",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }

                // --- 2. CHỈ SỐ HOẠT ĐỘNG ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(value = (user?.adopt_count ?: 0).toString(), label = "Đã nhận nuôi")
                    StatItem(value = (user?.post_count ?: 0).toString(), label = "Bài viết")
                }

                Spacer(Modifier.height(30.dp))

                // --- 3. CÁC MỤC LINK ĐIỀU HƯỚNG ---
                SectionTitle("Hoạt động của tôi")
                ProfileLinkItem(Icons.Default.FavoriteBorder, "Thú cưng đang theo dõi", Color(0xFF80DEEA)) {
                    // navController.navigate("favorites")
                }
                ProfileLinkItem(Icons.Default.History, "Lịch sử nhận nuôi", Color(0xFFFFCC80)) {
                    // navController.navigate("adopt_history")
                }

                SectionTitle("Cài đặt & Hỗ trợ")
                ProfileLinkItem(Icons.Default.Edit, "Chỉnh sửa thông tin", Color(0xFFEEEEEE)) {
                    navController.navigate("edit_profile")
                }

                // --- 4. ĐĂNG XUẤT ---
                ProfileLinkItem(Icons.Default.ExitToApp, "Đăng xuất", Color(0xFFFFEBEE), isLogout = true) {
                    SessionManager(context).logout()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }

                Spacer(Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun ProfileLinkItem(
    icon: ImageVector,
    title: String,
    bgColor: Color,
    isLogout: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLogout) Color(0xFFFFEBEE) else Color(0xFFF5F5F5)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(bgColor, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = if (isLogout) Color.Red else Color.DarkGray
                )
            }
            Spacer(Modifier.width(16.dp))
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                color = if (isLogout) Color.Red else Color.Black
            )
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
        }
    }
}

@Composable
fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF8D4000))
        Text(label, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(24.dp, 16.dp, 24.dp, 8.dp)
    )
}