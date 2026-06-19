package com.example.project_3.ui.admin

import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.project_3.data.local.SessionManager
import com.example.project_3.viewmodel.AdminProfileViewModel

// --- GÓI GỌN MÀU SẮC VÀO OBJECT ĐỂ TRÁNH LỖI TRÙNG TÊN (CONFLICTING DECLARATIONS) ---
private object AdminProfileColors {
    val PrimaryColor = Color(0xFF006F69)
    val PrimaryContainer = Color(0xFFA6F2EA)
    val OnPrimaryContainer = Color(0xFF00201E)
    val OnPrimaryColor = Color(0xFFFFFFFF)
    val SecondaryContainer = Color(0xFFCCE8E5)
    val OnSecondaryContainer = Color(0xFF00201E)
    val SurfaceColor = Color(0xFFFBF9F8)
    val OnSurface = Color(0xFF191C1C)
    val OnSurfaceVariant = Color(0xFF3F4947)
    val SurfaceContainerHigh = Color(0xFFE0E3E2)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProfileScreen(
    currentAdminId: String,
    navController: NavController,
    viewModel: AdminProfileViewModel
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val sessionManager = remember { SessionManager(context) }
    // Gọi lấy thông tin khi Screen được mở hoặc ID Admin thay đổi
    LaunchedEffect(currentAdminId) {
        if (currentAdminId.isNotEmpty()) {
            viewModel.loadAdminProfile(currentAdminId)
        }
    }

    // CHỐT CHẶN BẢO MẬT: Lắng nghe trạng thái đăng xuất từ ViewModel
    LaunchedEffect(uiState.isLoggedOut) {
        if (uiState.isLoggedOut) {
            // Điều hướng về màn hình login và xóa sạch hàng đợi BackStack
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    // SỬA TẠI ĐÂY: Sử dụng đúng hàm viewModel.clearToast() để làm sạch State thông báo
    LaunchedEffect(viewModel.toastMessage.value) {
        viewModel.toastMessage.value?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Build, contentDescription = null, tint = AdminProfileColors.PrimaryColor)
                        Text("Admin Portal", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = AdminProfileColors.PrimaryColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AdminProfileColors.SurfaceColor),
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(AdminProfileColors.PrimaryContainer)
                    ) {
                        val avatarUrl = uiState.adminInfo?.avatar ?: "https://lh3.googleusercontent.com/aida-public/AB6AXuAaJUK41CvMxZYDEDYRwN42j4cGA3sYMrporFgX5RTgL3vzKjyEfJHgOd-5uT_nJg0dQFkok7lXlQwcxlmZbBgV01_jM-KrbstmjaqYs8pGHnFGrDLsih2PMGGGdnWT3kR1ZN4QkIHaCid-Y8wjB8jAi4OG11dveCpZQbApvO1Xx8C8ercxgaedIR9P0MiKUSJh4uTiOvyQWGTgrZmY4O7bCNy3hS24PSKjQG63_wilEzPK_d-_AUvTgjew7WD9NVKBhxKnaOJqp7Y"
                        Image(
                            painter = rememberAsyncImagePainter(avatarUrl),
                            contentDescription = "Mini Avatar",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AdminProfileColors.SurfaceColor)
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = AdminProfileColors.PrimaryColor)
            } else if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage ?: "",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp),
                    textAlign = TextAlign.Center
                )
            } else {
                val admin = uiState.adminInfo
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // --- Hero Section ---
                    Box(modifier = Modifier.padding(vertical = 16.dp)) {
                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .padding(4.dp)
                        ) {
                            val mainAvatar = admin?.avatar ?: "https://lh3.googleusercontent.com/aida-public/AB6AXuBG8LMqH0wEElZY2RG0QBbNsCTjsSqaL3O42BqG8DyOQWkJGU6cKyTXZa_Pceh9hpsBEBOKcGeNdHjYOOSnqdGwavmAjyjI0LD6c8G5HvWfWSRrZPbMXgGZ9Kc6-yjqA4C0hXr-eC9Zw0nQmGaD7GUt0l28L3x7Cj6eCJ9XqhO_HfoVx4C4BK0ozDMaBtL16hVpZS_p7c8npzzx6zzkU4eeiFtvCn1ccAjX3D8OviYg6gI1gc5kHBvnO5dZK5Y_8dOyjUgXGwd71Vo"
                            Image(
                                painter = rememberAsyncImagePainter(mainAvatar),
                                contentDescription = "Admin Avatar",
                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset(x = (-4).dp, y = (-4).dp)
                                .background(AdminProfileColors.PrimaryColor, CircleShape)
                                .padding(6.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AdminProfileColors.OnPrimaryColor, modifier = Modifier.size(20.dp))
                        }
                    }

                    Text(
                        text = admin?.fullname ?: admin?.username ?: "Admin",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = AdminProfileColors.OnSurface,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    Row(
                        modifier = Modifier
                            .background(AdminProfileColors.SecondaryContainer, RoundedCornerShape(99.dp))
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.AccountCircle, contentDescription = null, tint = AdminProfileColors.OnSecondaryContainer, modifier = Modifier.size(18.dp))
                        Text(
                            text = if (admin?.role == "admin") "Senior Administrator" else "Staff",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AdminProfileColors.OnSecondaryContainer
                        )
                    }

                    Text(
                        text = "Managing regional pet rescue operations and ensuring every animal finds a loving forever home.",
                        fontSize = 16.sp,
                        color = AdminProfileColors.OnSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(top = 12.dp, bottom = 24.dp)
                            .widthIn(max = 320.dp)
                    )

                    // --- Bento Grid Menu ---
                    BentoSectionContainer(title = "General") {
                        BentoMenuItem(
                            icon = Icons.Default.Person,
                            iconBg = Color(0xFFE0F2F1),
                            iconTint = Color(0xFF006F69),
                            title = "Personal Information",
                            subtitle = "Quản lý thông tin hồ sơ cá nhân",
                            onClick = {
                                // TRUY XUẤT THÔNG QUA uiState.adminInfo
                                val currentId = uiState.adminInfo?.id_user?.toString() ?: currentAdminId
                                if (currentId.isNotEmpty()) {
                                    navController.navigate("admin_edit_profile/$currentId")
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        BentoMenuItem(
                            icon = Icons.Default.Lock,
                            iconBg = AdminProfileColors.SecondaryContainer,
                            iconTint = AdminProfileColors.OnSecondaryContainer,
                            title = "Security & Password",
                            subtitle = "Manage credentials and 2FA",
                            onClick = { /* Điều hướng đổi mật khẩu */ }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    BentoSectionContainer(title = "Preferences") {
                        BentoMenuItem(
                            icon = Icons.Default.Settings,
                            iconBg = AdminProfileColors.SurfaceContainerHigh,
                            iconTint = AdminProfileColors.OnSurfaceVariant,
                            title = "System Settings",
                            subtitle = "Interface and portal behavior",
                            onClick = { /* Cài đặt hệ thống */ }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        BentoMenuItem(
                            icon = Icons.Default.NotificationsActive,
                            iconBg = AdminProfileColors.PrimaryContainer.copy(alpha = 0.4f),
                            iconTint = AdminProfileColors.OnPrimaryContainer,
                            title = "Notification Preferences",
                            subtitle = "Stay updated on rescue cases",
                            onClick = { /* Cài đặt thông báo */ }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            sessionManager.logout() // Xóa SharedPreferences cục bộ
                            viewModel.logout(sessionManager)       // Thay đổi UI State đăng xuất
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBA1A1A)),
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Log Out Account", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun BentoSectionContainer(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = AdminProfileColors.PrimaryColor,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                content = content
            )
        }
    }
}

@Composable
fun BentoMenuItem(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = AdminProfileColors.OnSurface)
            Text(text = subtitle, fontSize = 12.sp, color = AdminProfileColors.OnSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }

        Icon(imageVector = Icons.Default.KeyboardArrowRight, tint = AdminProfileColors.OnSurfaceVariant, contentDescription = null)
    }
}