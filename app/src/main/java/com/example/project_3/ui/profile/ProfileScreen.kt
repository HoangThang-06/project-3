package com.example.project_3.ui.profile

import androidx.compose.foundation.background
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.project_3.R
import com.example.project_3.data.local.SessionManager
import com.example.project_3.viewmodel.ProfileViewModel
import com.example.project_3.viewmodel.factory.ProfileViewModelFactory

@Composable
fun ProfileScreen() {
    val context = LocalContext.current
    val sessionManager = SessionManager(context)
    val viewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(sessionManager)
    )

    val user = viewModel.user
    val baseUrl = "http://10.0.2.2/project-3/upload"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFBFBFB))
            .verticalScroll(rememberScrollState())
    ) {
        // --- 1. HEADER & AVATAR ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                AsyncImage(
                    model = "$baseUrl${user?.avatar ?: "/avatars/default.png"}",
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentScale = ContentScale.Crop
                )
                // Icon tích xanh hoặc camera tùy ý
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF00897B),
                    modifier = Modifier
                        .size(28.dp)
                        .background(Color.White, CircleShape)
                        .padding(2.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = user?.fullname ?: "Chưa cập nhật tên",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "Thành viên từ 12/2022", // Có thể lấy từ user?.create_at nếu có
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        // --- 2. STATS ROW (Đã nhận nuôi, Bài viết, Đóng góp) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem("03", "Đã nhận nuôi")
            Divider(modifier = Modifier.height(30.dp).width(1.dp), color = Color.LightGray)
            StatItem("12", "Bài viết")
            Divider(modifier = Modifier.height(30.dp).width(1.dp), color = Color.LightGray)
            StatItem("2.4k", "Đóng góp")
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- 3. HOẠT ĐỘNG CỦA TÔI ---
        SectionTitle("Hoạt động của tôi")
        ActionItem(Icons.Default.FavoriteBorder, "Thú cưng đang theo dõi", Color(0xFF80DEEA))
        ActionItem(Icons.Default.History, "Lịch sử nhận nuôi", Color(0xFFFFCC80))
        ActionItem(Icons.Default.Assignment, "Các báo cáo cứu hộ đã gửi", Color(0xFFE0E0E0))

        Spacer(modifier = Modifier.height(24.dp))

        // --- 4. CÀI ĐẶT & HỖ TRỢ ---
        SectionTitle("Cài đặt & Hỗ trợ")
        ActionItem(Icons.Default.Edit, "Chỉnh sửa thông tin", Color(0xFFF5F5F5))
        ActionItem(Icons.Default.NotificationsNone, "Thông báo", Color(0xFFF5F5F5))
        ActionItem(Icons.Default.HelpOutline, "Trung tâm trợ giúp", Color(0xFFF5F5F5))

        // --- 5. LOGOUT ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color.Red)
                Spacer(modifier = Modifier.width(16.dp))
                Text("Đăng xuất", color = Color.Red, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(100.dp)) // Padding dưới để không bị che bởi bottom bar
    }
}

@Composable
fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF8D4000))
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
    )
}

@Composable
fun ActionItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, iconBgColor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5).copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(iconBgColor, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Color.DarkGray, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, modifier = Modifier.weight(1f), fontSize = 15.sp)
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
        }
    }
}