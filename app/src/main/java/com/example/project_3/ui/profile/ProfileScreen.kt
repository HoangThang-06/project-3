package com.example.project_3.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable // Sửa lỗi clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector // Sửa lỗi ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController // Sửa lỗi NavController
import coil.compose.AsyncImage
import com.example.project_3.data.local.SessionManager
import com.example.project_3.viewmodel.ProfileViewModel
import com.example.project_3.viewmodel.factory.ProfileViewModelFactory

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory(SessionManager(LocalContext.current)))
) {
    val user = viewModel.user
    val context = LocalContext.current
    val baseUrl = "http://10.0.2.2/project-3/upload"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFBFBFB))
            .verticalScroll(rememberScrollState())
    ) {
        // --- 1. THÔNG TIN CÁ NHÂN (Dữ liệu từ Session/API) ---
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = "$baseUrl${user?.avatar ?: "/avatars/default.png"}",
                contentDescription = null,
                modifier = Modifier.size(100.dp).clip(CircleShape).background(Color.White),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.height(12.dp))
            Text(user?.fullname ?: "Đang tải...", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Thành viên từ: ${user?.username ?: ""}", fontSize = 13.sp, color = Color.Gray)
        }

        // --- 2. CHỈ SỐ HOẠT ĐỘNG (Dữ liệu thật từ SQL COUNT) ---
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            StatItem(user?.status ?: "0", "Đã nhận nuôi") // Giả sử dùng field status chứa số lượng
            StatItem("12", "Bài viết")
            StatItem("2.4k", "Đóng góp")
        }

        Spacer(Modifier.height(30.dp))

        // --- 3. CÁC MỤC LINK (Bấm vào để chuyển màn hình) ---
        SectionTitle("Hoạt động của tôi")
        ProfileLinkItem(Icons.Default.FavoriteBorder, "Thú cưng đang theo dõi", Color(0xFF80DEEA)) {
            // navController.navigate("favorites")
        }
        ProfileLinkItem(Icons.Default.History, "Lịch sử nhận nuôi", Color(0xFFFFCC80)) {
            // navController.navigate("adopt_history")
        }

        SectionTitle("Cài đặt & Hỗ trợ")
        ProfileLinkItem(Icons.Default.Edit, "Chỉnh sửa thông tin", Color(0xFFEEEEEE)) {
            // navController.navigate("edit_profile")
        }

        // --- 4. ĐĂNG XUẤT ---
        ProfileLinkItem(Icons.Default.ExitToApp, "Đăng xuất", Color(0xFFFFEBEE), isLogout = true) {
            SessionManager(context).logout()
            // Chuyển về màn Login
            navController.navigate("login") { popUpTo(0) }
        }

        Spacer(Modifier.height(80.dp))
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
            .clickable { onClick() }, // Biến Card thành nút bấm
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if(isLogout) Color(0xFFFFEBEE) else Color(0xFFF5F5F5))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(36.dp).background(bgColor, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, modifier = Modifier.size(20.dp), tint = if(isLogout) Color.Red else Color.DarkGray)
            }
            Spacer(Modifier.width(16.dp))
            Text(title, modifier = Modifier.weight(1f), color = if(isLogout) Color.Red else Color.Black)
            Icon(Icons.Default.ChevronRight, null, tint = Color.Gray)
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
    Text(title, fontWeight = FontWeight.Bold, modifier = Modifier.padding(24.dp, 16.dp, 24.dp, 8.dp))
}