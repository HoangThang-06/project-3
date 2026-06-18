package com.example.project_3.ui.home

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.navigation.NavController
import com.example.project_3.R
import com.example.project_3.ui.adopt.AdoptScreen
import com.example.project_3.ui.social.SocialScreen
import com.example.project_3.viewmodel.SocialViewModel
import com.example.project_3.ui.profile.ProfileScreen
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import com.example.project_3.data.model.EventModel
import com.example.project_3.data.model.Pet
import com.example.project_3.data.model.KnowledgeModel
import com.example.project_3.ui.report.ReportScreen
import com.example.project_3.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    mainNavController: NavController
) {
    val context = LocalContext.current

    // Quản lý trạng thái tab đang chọn
    var selectedItem by rememberSaveable { mutableIntStateOf(0) }
    val items = listOf("Home", "Adopt", "Report", "Social", "Profile")

    // QUẢN LÝ TRẠNG THÁI ĐÓNG/MỞ BOTTOM SHEET THÔNG BÁO (DÙNG CHUNG CHO TẤT CẢ CÁC TAB)
    var showNotificationSheet by remember { mutableStateOf(false) }

    // Trạng thái lưu bài viết kiến thức đang được chọn để xem chi tiết
    var selectedKnowledge by remember { mutableStateOf<KnowledgeModel?>(null) }

    // Khởi tạo các ViewModel
    val socialViewModel: SocialViewModel = viewModel()
    val homeViewModel: HomeViewModel = viewModel()

    // 💡 TỐI ƯU 1: Theo dõi trạng thái selectedItem. Mỗi khi người dùng chuyển đổi tab
    // (ví dụ từ mạng xã hội Social quay lại Home), hệ thống sẽ chủ động kéo số liệu mới từ DB.
    LaunchedEffect(selectedItem) {
        homeViewModel.fetchNotifications(userId = 1)
    }

    val icons = listOf(
        Icons.Default.Home,
        Icons.Default.Pets,
        Icons.Default.LocationOn,
        Icons.Default.ChatBubble,
        Icons.Default.Person
    )

    if (selectedItem == 0 && selectedKnowledge != null) {
        KnowledgeDetailScreen(
            knowledge = selectedKnowledge!!,
            onBackClick = { selectedKnowledge = null }
        )
    } else {
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
                        // QUAN SÁT SỐ LƯỢNG THÔNG BÁO CHƯA ĐỌC TỪ VIEWMODEL
                        val unreadCount by homeViewModel.unreadNotificationCount

                        IconButton(onClick = {
                            // Khi nhấn chuông: Vừa tự làm mới danh sách từ MySQL, vừa mở Sheet hiển thị
                            homeViewModel.fetchNotifications(userId = 1)
                            showNotificationSheet = true
                        }) {
                            // Badge đè góc hiển thị số lượng chưa đọc như Facebook
                            BadgedBox(
                                badge = {
                                    if (unreadCount > 0) {
                                        Badge(
                                            containerColor = Color.Red,
                                            contentColor = Color.White
                                        ) {
                                            Text(text = unreadCount.toString(), fontSize = 10.sp)
                                        }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.NotificationsNone, contentDescription = "Thông báo")
                            }
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
                if (selectedItem == 0) {
                    FloatingActionButton(
                        onClick = { /* Xử lý thêm pet */ },
                        containerColor = Color(0xFF8D4000),
                        contentColor = Color.White,
                        shape = CircleShape
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Thêm Pet")
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
                        HomeContent(
                            viewModel = homeViewModel,
                            onPetClick = { idPet -> mainNavController.navigate("pet_detail/$idPet") },
                            onEmergencyClick = { selectedItem = 2 },
                            onKnowledgeClick = { knowledgeItem -> selectedKnowledge = knowledgeItem }
                        )
                    }
                    1 -> AdoptScreen(navController = mainNavController)
                    2 -> ReportScreen(onReportSuccess = {})
                    3 -> SocialScreen(socialViewModel)
                    4 -> ProfileScreen(navController = mainNavController)
                }
            }
        }

        // ĐẶT BOTTOMSHEET TOÀN CỤC: Đọc dữ liệu từ API thông qua HomeViewModel
        if (showNotificationSheet) {
            NotificationBottomSheet(
                viewModel = homeViewModel,
                onDismiss = { showNotificationSheet = false }
            )
        }
    }
}

// --- COMPOSABLE BOTTOM SHEET ĐỌC THÔNG BÁO TỪ SERVER QUA REPO/VIEWMODEL ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationBottomSheet(
    viewModel: HomeViewModel,
    onDismiss: () -> Unit
) {
    val notifications by viewModel.dbNotificationList

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Thông báo tương tác",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (notifications.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Bạn chưa có thông báo tương tác nào.", color = Color.Gray, fontSize = 14.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(notifications) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                // Nếu thông báo chưa đọc (is_read = 0) thì tô nền nhạt phân biệt
                                .background(
                                    if (item.is_read == 0) Color(0xFFFFF5F5) else Color(0xFFF9F9F9),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (item.is_read == 0) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(Color.Red, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                            }
                            Column {
                                // 💡 TỐI ƯU 2: Cơ chế phòng vệ chữ (Fallback).
                                // Nếu chuỗi 'content' từ PHP bị rỗng do bất kỳ lý do gì,
                                // Android sẽ tự động căn cứ theo 'type' để sinh câu thông báo chuẩn xác.
                                val textDisplay = if (item.content.isNotEmpty()) {
                                    item.content
                                } else {
                                    if (item.type == "like") "Một người dùng đã thích bài viết của bạn."
                                    else "Một người dùng đã bình luận vào bài viết của bạn."
                                }

                                Text(
                                    text = textDisplay,
                                    fontWeight = if (item.is_read == 0) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 14.sp,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = item.create_at, fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// --- COMPOSABLE NỘI DUNG RUỘT CỦA TRANG CHỦ (GIỮ NGUYÊN HOÀN TOÀN) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    viewModel: HomeViewModel,
    onPetClick: (Int) -> Unit,
    onEmergencyClick: () -> Unit,
    onKnowledgeClick: (KnowledgeModel) -> Unit
) {
    val greeting by viewModel.greetingText
    val events by viewModel.eventsList
    val featuredPets by viewModel.featuredPets
    val knowledgeList by viewModel.knowledgeList
    var searchText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp)
    ) {
        // 1. LỜI CHÀO
        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
            Text(text = greeting, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(
                text = "Hãy tìm kiếm một người bạn bốn chân mới cho gia đình mình hôm nay nhé.",
                fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp)
            )
        }

        // 2. THANH TÌM KIẾM
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = { Text("Tìm kiếm giống chó, mèo...", color = Color.LightGray, fontSize = 14.sp) },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
            shape = RoundedCornerShape(28.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = Color(0xFFF2F2F2),
                unfocusedContainerColor = Color(0xFFF2F2F2)
            ),
            singleLine = true
        )

        // 3. SỰ KIỆN SẮP TỚI
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Sự kiện sắp tới", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text("Xem tất cả", fontSize = 13.sp, color = Color.Gray, modifier = Modifier.clickable { })
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(events) { event ->
                EventCard(event)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 4. KIẾN THỨC NUÔI THÚ CƯNG
        Text(
            text = "Kiến thức nuôi thú cưng", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))

        knowledgeList.forEach { knowledgeItem ->
            KnowledgeItem(
                knowledge = knowledgeItem,
                onClick = { onKnowledgeClick(knowledgeItem) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 5. THÚ CƯNG NỔI BẬT
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Thú Cưng Nổi Bật", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Icon(imageVector = Icons.Default.FilterList, contentDescription = null, tint = Color(0xFF8D4000))
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(featuredPets) { pet ->
                FeaturedPetCard(pet = pet, onClick = { onPetClick(pet.id_pet) })
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 6. BANNER KHẨN CẤP
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).clickable { onEmergencyClick() },
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE4E1)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Hỗ Trợ Khẩn Cấp", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF8B0000))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Bạn phát hiện động vật gặp nạn hoặc cần cứu trợ y tế ngay lập tức?",
                        fontSize = 13.sp, color = Color(0xFFCD5C5C)
                    )
                }
                Box(
                    modifier = Modifier.size(44.dp).background(Color(0xFF8B0000), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
fun KnowledgeItem(knowledge: KnowledgeModel, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = knowledge.image,
                contentDescription = null,
                modifier = Modifier.size(64.dp).clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.ic_paw_print),
                error = painterResource(id = R.drawable.ic_paw_print)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = knowledge.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Đọc thêm >", fontSize = 13.sp, color = Color(0xFF8D4000), fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun FeaturedPetCard(pet: Pet, onClick: () -> Unit) {
    Card(
        modifier = Modifier.width(160.dp).clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                model = pet.image, contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier.padding(10.dp).size(32.dp).background(Color.White.copy(alpha = 0.8f), CircleShape).align(Alignment.TopEnd),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.FavoriteBorder, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
            }
            Column(modifier = Modifier.fillMaxWidth().padding(top = 155.dp, start = 12.dp, end = 12.dp, bottom = 12.dp)) {
                Text(text = pet.name_pet, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(4.dp))
                val speciesName = when(pet.species.lowercase()) { "dog" -> "Chó"; "cat" -> "Mèo"; else -> "Khác" }
                Text(text = "$speciesName • ${pet.age} tuổi", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun EventCard(event: EventModel) {
    val context = LocalContext.current
    Card(
        modifier = Modifier.width(240.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            AsyncImage(
                model = event.image_url ?: R.drawable.ic_paw_print,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(110.dp).clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = event.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${event.date} • ${event.location}", fontSize = 11.sp, color = Color.Gray, maxLines = 1)
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { scheduleEventNotification(context, event.title, event.date) },
                    modifier = Modifier.fillMaxWidth().height(32.dp),
                    contentPadding = PaddingValues(vertical = 0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFDECE3), contentColor = Color(0xFF8D4000)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Notifications, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Đặt lịch", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}