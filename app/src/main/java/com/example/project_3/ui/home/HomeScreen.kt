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
import androidx.lifecycle.viewmodel.compose.viewModel // Nhớ thêm import này
import com.example.project_3.R
import com.example.project_3.ui.adopt.AdoptScreen
import com.example.project_3.ui.social.SocialScreen // Import SocialScreen của bạn
import com.example.project_3.viewmodel.SocialViewModel // Import ViewModel
import com.example.project_3.ui.profile.ProfileScreen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf("Home", "Adopt", "Report", "Social", "Profile")

    // Khởi tạo ViewModel ở đây để quản lý dữ liệu Social
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
                    // Quả chuông thông báo
                    IconButton(onClick = {
                        // Sau này sẽ điều hướng sang màn hình NotificationScreen
                    }) {
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
        // Lưu ý: Đã bỏ FloatingActionButton ở đây vì trong SocialScreen đã có nút "Đăng bài" riêng
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
                    AdoptScreen()
                }
                2 -> Text("Màn hình Báo cáo", modifier = Modifier.align(Alignment.Center))
                3 -> {
                    SocialScreen(socialViewModel)
                }
                4 ->{
                    ProfileScreen()
                }
            }
        }
    }
}