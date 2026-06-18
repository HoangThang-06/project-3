package com.example.project_3.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController // SỬA: Dùng NavController thay vì import rememberNavController
import com.example.project_3.R
import com.example.project_3.ui.adopt.AdoptScreen
import com.example.project_3.ui.social.SocialScreen
import com.example.project_3.viewmodel.SocialViewModel
import com.example.project_3.ui.profile.ProfileScreen
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.project_3.ui.report.ReportScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    mainNavController: NavController // 1. SỬA TẠI ĐÂY: Nhận NavController chính từ MainActivity truyền xuống
) {
    // XÓA dòng: val navController = rememberNavController() cũ ở đây đi

    // Quản lý trạng thái tab đang chọn
    var selectedItem by rememberSaveable { mutableIntStateOf(0) }
    val items = listOf("Home", "Adopt", "Report", "Social", "Profile")

    // Khởi tạo ViewModel cho Social (Mạng xã hội)
    val socialViewModel: SocialViewModel = viewModel()

    val icons = listOf(
        Icons.Default.Home,
        Icons.Default.Pets,
        Icons.Default.LocationOn,
        Icons.Default.ChatBubble,
        Icons.Default.Person
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_paw_print),
                            contentDescription = null,
                            tint = Color(0xFF8D4000),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Paws & Hearts",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF8D4000),
                            fontSize = 20.sp
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Xử lý thông báo */ }) {
                        Icon(Icons.Default.NotificationsNone, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedItem == index,
                        onClick = { selectedItem = index },
                        icon = { Icon(icons[index], contentDescription = item) },
                        label = { Text(item) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF8D4000),
                            indicatorColor = Color(0xFFFDECE3)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedItem) {
                0 -> {
                    Text("Nội dung Trang chủ", modifier = Modifier.align(Alignment.Center))
                }
                1 -> {
                    // 2. SỬA TẠI ĐÂY: Truyền mainNavController tổng vào AdoptScreen
                    AdoptScreen(navController = mainNavController)
                }
                2 -> {
                    // ĐÃ SỬA: Thay thế Text cứng bằng Màn hình Báo Cáo Thú Cưng thực tế
                    ReportScreen(
                        onReportSuccess = {
                        }
                    )
                }
                3 -> {
                    SocialScreen(socialViewModel)
                }
                4 -> {
                    // 3. SỬA TẠI ĐÂY: Truyền mainNavController tổng vào ProfileScreen
                    ProfileScreen(navController = mainNavController)
                }
            }
        }
    }
}