package com.example.project_3.ui.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.project_3.data.local.SessionManager
import com.example.project_3.data.model.AdoptionHistory
import com.example.project_3.data.remote.RetrofitClient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdoptHistoryScreen(navController: NavController) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Lấy ID người dùng đang đăng nhập để truyền lên API
    val sessionManager = remember { SessionManager(context) }
    val userId = sessionManager.getUserId()

    // Quản lý danh sách động từ DB gửi về
    var historyList by remember { mutableStateOf<List<AdoptionHistory>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Tất cả", "Đang xử lý", "Đã chấp nhận")

    val baseUrl = "http://10.0.2.2/project-3/upload/" // Đường dẫn ảnh trên máy ảo Android

    // Tự động gọi API khi vào màn hình
    LaunchedEffect(userId) {
        if (userId == -1) {
            Toast.makeText(context, "Lỗi: Chưa tìm thấy phiên đăng nhập!", Toast.LENGTH_SHORT).show()
            isLoading = false
            return@LaunchedEffect
        }

        try {
            val response = RetrofitClient.api.getAdoptHistory(userId)
            if (response.success && response.history != null) {
                historyList = response.history
            } else {
                Toast.makeText(context, response.message ?: "Lỗi lấy dữ liệu", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Lỗi kết nối mạng: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            isLoading = false
        }
    }

    // Bộ lọc danh sách dựa trên các Tab trên giao diện
    val filteredList = remember(selectedTabIndex, historyList) {
        when (selectedTabIndex) {
            1 -> historyList.filter { it.status == "pending" }
            2 -> historyList.filter { it.status == "approved" }
            else -> historyList
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(end = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Paws & Hearts",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF8D4000)
                        )
                        IconButton(onClick = { }) {
                            Icon(Icons.Default.Notifications, contentDescription = null, tint = Color(0xFF8D4000))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFFBFBFB)
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF8D4000)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                        .verticalScroll(scrollState)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Lịch sử nhận nuôi", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Theo dõi tiến trình các hồ sơ nhận nuôi của bạn.", fontSize = 14.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.height(24.dp))

                    // --- CHỌN TAB LỌC ---
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        tabs.forEachIndexed { index, title ->
                            val isSelected = selectedTabIndex == index
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (isSelected) Color(0xFFFD8C45) else Color(0xFFEEEEEE))
                                    .clickable { selectedTabIndex = index },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isSelected) Color.White else Color.DarkGray
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // --- DANH SÁCH DANH MỤC HỒ SƠ ---
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (filteredList.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Không có hồ sơ nào", color = Color.Gray)
                            }
                        } else {
                            filteredList.forEach { item ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AsyncImage(
                                            model = if (item.imageUrl.startsWith("http")) item.imageUrl else "$baseUrl${item.imageUrl}",
                                            contentDescription = null,
                                            modifier = Modifier.size(80.dp).clip(RoundedCornerShape(12.dp)),
                                            contentScale = ContentScale.Crop
                                        )

                                        Spacer(modifier = Modifier.width(16.dp))

                                        Row(
                                            modifier = Modifier.weight(1f),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(item.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                                Text("${item.breed} • ${item.age}", fontSize = 13.sp, color = Color.Gray, modifier = Modifier.padding(vertical = 2.dp))
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        painter = painterResource(id = android.R.drawable.ic_menu_my_calendar),
                                                        contentDescription = null,
                                                        tint = Color.Gray,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Đã nộp: ${item.dateSubmitted}", fontSize = 12.sp, color = Color.Gray)
                                                }
                                            }

                                            // ==========================================
                                            // ĐỌC VÀ MAP CHUẨN ĐỒNG BỘ THEO ENUM MỚI CỦA DB
                                            // ==========================================
                                            val (textStatus, containerColor, textColor) = when (item.status) {
                                                "pending" -> Triple("Đang xử lý", Color(0xFFFFE0B2), Color(0xFFE65100))
                                                "approved" -> Triple("Đã chấp nhận", Color(0xFFE8F5E9), Color(0xFF2E7D32))
                                                "rejected" -> Triple("Đã từ chối", Color(0xFFFFEBEE), Color(0xFFC62828))
                                                else -> Triple(item.status, Color(0xFFEEEEEE), Color.DarkGray)
                                            }

                                            Box(
                                                modifier = Modifier
                                                    .padding(start = 8.dp)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(containerColor)
                                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(text = textStatus, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textColor)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}