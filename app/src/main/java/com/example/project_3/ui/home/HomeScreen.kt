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
import com.example.project_3.R
import com.example.project_3.ui.adopt.AdoptScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf("Home", "Adopt", "Report", "Social", "Profile")

    // Lưu ý: Các icon này yêu cầu thư viện material-icons-extended đã thêm ở Bước 1
    val icons = listOf(
        Icons.Default.Home,
        Icons.Default.Pets,
        Icons.Default.LocationOn,
        Icons.Default.ChatBubble,
        Icons.Default.Person
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar( // Dùng CenterAligned cho giống mẫu của bạn
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
                    IconButton(onClick = { }) {
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { },
                containerColor = Color(0xFF8D4000),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    ) { innerPadding ->
        // Sử dụng Box để bao ngoài và áp dụng innerPadding từ Scaffold
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedItem) {
                0 -> {
                    // Màn hình Trang chủ (Bạn có thể tạo MainHomeScreen tương tự AdoptScreen)
                    Text("Nội dung Trang chủ lấy từ API", modifier = Modifier.align(Alignment.Center))
                }
                1 -> {
                    AdoptScreen()
                }
                2 -> Text("Màn hình Báo cáo", modifier = Modifier.align(Alignment.Center))
                3 -> Text("Màn hình Mạng xã hội", modifier = Modifier.align(Alignment.Center))
                4 -> Text("Màn hình Cá nhân", modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}
