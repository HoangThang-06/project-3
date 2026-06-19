package com.example.project_3.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.project_3.R
import com.example.project_3.data.local.SessionManager
import com.example.project_3.viewmodel.ProfileViewModel
import com.example.project_3.viewmodel.factory.ProfileViewModelFactory

// 🔥 HÀM HELPER: Kiểm tra và chuẩn hóa đường dẫn Avatar cho Profile, ưu tiên ảnh Drawable mặc định
fun getProfileAvatarUrl(avatarPath: String?): Any {
    if (avatarPath.isNullOrEmpty() || avatarPath == "default" || avatarPath == "null" || avatarPath.contains("default.png")) {
        return R.drawable.ic_default_avatar
    }

    val baseUrl = "http://10.0.2.2/project-3"

    return when {
        avatarPath.startsWith("/uploads") -> "$baseUrl$avatarPath"
        avatarPath.startsWith("uploads") -> "$baseUrl/$avatarPath"
        else -> if (avatarPath.startsWith("/")) "$baseUrl/uploads$avatarPath" else "$baseUrl/uploads/$avatarPath"
    }
}

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory(SessionManager(LocalContext.current)))
) {
    val user = viewModel.user
    val isLoading = viewModel.isLoading

    val context = LocalContext.current

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

                    // 💡 CẬP NHẬT: Bao bọc Avatar bằng Box để xếp chồng nút Đổi ảnh lên trên góc nhỏ
                    Box(
                        modifier = Modifier
                            .size(106.dp) // Tăng nhẹ kích thước bọc ngoài để tránh bị cắt lẹm nút camera
                            .clickable {
                                // Xử lý mở Image Picker tại đây hoặc điều hướng sang màn hình chỉnh sửa
                                navController.navigate("edit_profile")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        // Ảnh đại diện chính
                        AsyncImage(
                            model = getProfileAvatarUrl(user?.avatar),
                            contentDescription = "User Avatar",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(2.dp, Color(0xFFFDECE3), CircleShape),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(id = R.drawable.ic_default_avatar),
                            error = painterResource(id = R.drawable.ic_default_avatar)
                        )

                        // 📸 NÚT ĐỔI ẢNH (Góc dưới bên phải)
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .background(Color(0xFF8D4000), CircleShape)
                                .border(2.dp, Color.White, CircleShape)
                                .align(Alignment.BottomEnd)
                                .clickable {
                                    // Xử lý mở Image Picker giống hệt như bấm vào avatar
                                    navController.navigate("edit_profile")
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Change Avatar",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

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
                    navController.navigate("favorite_pets")
                }

                ProfileLinkItem(Icons.Default.History, "Lịch sử nhận nuôi", Color(0xFFFFCC80)) {
                    navController.navigate("adopt_history")
                }

                ProfileLinkItem(Icons.Default.ListAlt, "Lịch sử bài viết", Color(0xFFC5CAE9)) {
                    navController.navigate("post_history")
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