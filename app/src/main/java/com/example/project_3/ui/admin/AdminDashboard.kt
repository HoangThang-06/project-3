package com.example.project_3.ui.admin

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.rememberAsyncImagePainter
import com.example.project_3.data.model.AdminActivity
import com.example.project_3.viewmodel.AdminDashboardViewModel

val DashPrimaryColor = Color(0xFF8D4000)
val DashPrimaryContainer = Color(0xFFFFDBC9)
val DashOnPrimaryContainer = Color(0xFF331400)
val DashSecondaryColor = Color(0xFF006A65)
val DashSecondaryContainer = Color(0xFF79F3EA)
val DashBackgroundColor = Color(0xFFFBFBFB)
val DashSurfaceContainer = Color(0xFFEFEDED)
val DashSurfaceContainerLowest = Color(0xFFFFFFFF)
val DashErrorColor = Color(0xFFBA1A1A)
val DashErrorContainer = Color(0xFFFFDAD6)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboard(
    navController: NavController,
    viewModel: AdminDashboardViewModel = viewModel()
) {
    val context = LocalContext.current
    var showEventDialog by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Bộ phóng chạy trình chọn ảnh từ điện thoại
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        viewModel.eventImageUrl = uri?.toString() ?: ""
    }

    LaunchedEffect(Unit) {
        viewModel.fetchDashboardData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Paws & Hearts", color = DashPrimaryColor, fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                actions = {
                    Box(modifier = Modifier.padding(end = 8.dp)) {
                        IconButton(onClick = { }) {
                            Icon(imageVector = Icons.Outlined.Notifications, contentDescription = "Notifications", tint = Color(0xFF1B1C1C))
                        }
                        Box(modifier = Modifier.size(8.dp).background(DashErrorColor, CircleShape).border(2.dp, DashBackgroundColor, CircleShape).align(Alignment.TopEnd).offset(x = (-8).dp, y = 8.dp))
                    }
                    Box(modifier = Modifier.padding(end = 16.dp).size(32.dp).background(DashPrimaryContainer, CircleShape)) {
                        Box(modifier = Modifier.fillMaxSize().background(Color.Gray))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DashBackgroundColor)
            )
        },
        bottomBar = {
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
            NavigationBar(containerColor = DashSurfaceContainerLowest, tonalElevation = 8.dp) {
                data class BottomItem(val title: String, val route: String, val icon: ImageVector)
                val items = listOf(
                    BottomItem("Dash", "admin_home", Icons.Default.Home),
                    BottomItem("Pets", "admin_manage_pet", Icons.Default.Pets),
                    BottomItem("Apps", "admin_adopt", Icons.Default.Menu),
                    BottomItem("Social", "admin_social", Icons.Default.Share),
                    BottomItem("Users", "admin_manage_user", Icons.Default.People)
                )
                items.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            if (currentRoute != item.route) {
                                navController.navigate(item.route) {
                                    launchSingleTop = true
                                    restoreState = true
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                }
                            }
                        },
                        icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                        label = { Text(text = item.title, fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DashOnPrimaryContainer,
                            selectedTextColor = Color(0xFF1B1C1C),
                            indicatorColor = DashPrimaryContainer.copy(alpha = 0.4f),
                            unselectedIconColor = Color(0xFF564338),
                            unselectedTextColor = Color(0xFF564338)
                        )
                    )
                }
            }
        },
        containerColor = DashBackgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // 1. KHU VỰC THỐNG KÊ (3 HÌNH CHỮ NHẬT NẰM NGANG)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                HorizontalStatCard(title = "Total Pets", value = viewModel.totalPets, icon = Icons.Default.Pets, iconBgColor = Color(0xFFFFDBC9), iconTintColor = DashPrimaryColor)
                HorizontalStatCard(title = "Adoptions", value = viewModel.totalAdoptions, icon = Icons.Default.Favorite, iconBgColor = DashSecondaryContainer, iconTintColor = DashSecondaryColor)
                HorizontalStatCard(title = "Rescues", value = viewModel.activeRescues, icon = Icons.Default.Warning, iconBgColor = DashErrorContainer, iconTintColor = DashErrorColor)
            }

            // 2. KHU VỰC HOẠT ĐỘNG GẦN ĐÂY
            RecentActivitiesSection(activities = viewModel.recentActivities)

            // 3. PENDING TASKS OVERVIEW
            PendingTasksSection()

            // 4. NÚT MỞ POPUP THÊM SỰ KIỆN MỚI
            Button(
                onClick = { showEventDialog = true },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DashPrimaryColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.AddCircle, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Thêm Sự Kiện Mới", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // 📅 POPUP HỘP THOẠI NHẬP THÔNG TIN SỰ KIỆN
        if (showEventDialog) {
            AlertDialog(
                onDismissRequest = { showEventDialog = false },
                title = { Text(text = "THÊM SỰ KIỆN MỚI", color = DashPrimaryColor, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
                    ) {
                        OutlinedTextField(
                            value = viewModel.eventTitle,
                            onValueChange = { viewModel.eventTitle = it },
                            label = { Text("Tên sự kiện (title) *") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )

                        OutlinedTextField(
                            value = viewModel.eventDate,
                            onValueChange = { viewModel.eventDate = it },
                            label = { Text("Ngày diễn ra (date) *") },
                            placeholder = { Text("Ví dụ: 25/12/2026") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )

                        OutlinedTextField(
                            value = viewModel.eventLocation,
                            onValueChange = { viewModel.eventLocation = it },
                            label = { Text("Địa điểm (location) *") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )

                        // Khu vực chọn ảnh trực quan
                        Text(text = "Hình ảnh sự kiện", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF564338))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .background(DashSurfaceContainer, RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFFDDC1B3), RoundedCornerShape(8.dp))
                                .clickable { imagePickerLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedImageUri == null) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null, tint = DashPrimaryColor, modifier = Modifier.size(32.dp))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Bấm để chọn ảnh từ máy", fontSize = 12.sp, color = Color.Gray)
                                }
                            } else {
                                Image(
                                    painter = rememberAsyncImagePainter(selectedImageUri),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                },
                // Tìm đến nút Confirm Button của AlertDialog
                confirmButton = {
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = DashPrimaryColor),
                        onClick = {
                            // 🔥 SỬA TẠI ĐÂY: Truyền thêm 'context' vào trước khối lambda
                            viewModel.addEvent(context) { isSuccess, message ->
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                if (isSuccess) {
                                    selectedImageUri = null // Reset ảnh cục bộ
                                    showEventDialog = false // Đóng popup
                                }
                            }
                        }
                    ) {
                        Text("Lưu lại")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEventDialog = false }) {
                        Text("Hủy bỏ", color = Color.Gray)
                    }
                }
            )
        }
    }
}

@Composable
fun HorizontalStatCard(title: String, value: String, icon: ImageVector, iconBgColor: Color, iconTintColor: Color) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DashSurfaceContainerLowest),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(modifier = Modifier.size(40.dp).background(iconBgColor, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = title, tint = iconTintColor, modifier = Modifier.size(22.dp))
                }
                Text(text = title, color = Color(0xFF564338), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
            Text(text = value, color = Color(0xFF1B1C1C), fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun RecentActivitiesSection(activities: List<AdminActivity>) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DashSurfaceContainerLowest),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Recent Activities", color = Color(0xFF1B1C1C), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Icon(imageVector = Icons.Default.History, contentDescription = "History", tint = Color(0xFF564338), modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            if (activities.isEmpty()) {
                Text(text = "Không có hoạt động nào gần đây.", color = Color.Gray, fontSize = 13.sp, modifier = Modifier.padding(vertical = 8.dp))
            } else {
                activities.forEachIndexed { index, activity ->
                    ActivityItem(
                        boldText = activity.boldText, normalText = activity.normalText, timeText = activity.timeText,
                        icon = if (activity.type == "adopt") Icons.Default.AssignmentTurnedIn else Icons.Default.LocationOn,
                        iconBg = if (activity.type == "adopt") DashSecondaryContainer else DashErrorContainer,
                        iconTint = if (activity.type == "adopt") DashSecondaryColor else DashErrorColor,
                        isBoldFirst = activity.type == "adopt"
                    )
                    if (index < activities.size - 1) Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun ActivityItem(boldText: String, normalText: String, timeText: String, icon: ImageVector, iconBg: Color, iconTint: Color, isBoldFirst: Boolean = true) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(modifier = Modifier.size(32.dp).background(iconBg, CircleShape), contentAlignment = Alignment.Center) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
        }
        Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
            Text(
                text = buildAnnotatedString {
                    if (isBoldFirst) {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append(boldText) }
                        append(normalText)
                    } else {
                        append(boldText)
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append(normalText) }
                    }
                },
                fontSize = 14.sp, color = Color(0xFF1B1C1C), modifier = Modifier.weight(1f), lineHeight = 18.sp
            )
            Text(text = timeText, fontSize = 10.sp, color = Color(0xFF564338), modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@Composable
fun PendingTasksSection() {
    Card(
        colors = CardDefaults.cardColors(containerColor = DashSurfaceContainer),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "PENDING TASKS", color = Color(0xFF1B1C1C), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Box(modifier = Modifier.background(DashPrimaryColor, CircleShape).padding(horizontal = 6.dp, vertical = 2.dp)) {
                    Text(text = "5 NEW", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            TaskItem(title = "3 Vet Checkups", icon = Icons.Default.MedicalServices)
            Spacer(modifier = Modifier.height(4.dp))
            TaskItem(title = "12 Review Apps", icon = Icons.Default.RateReview)
        }
    }
}

@Composable
fun TaskItem(title: String, icon: ImageVector) {
    Row(
        modifier = Modifier.fillMaxWidth().background(DashSurfaceContainerLowest, RoundedCornerShape(8.dp)).border(1.dp, Color(0xFFDDC1B3).copy(alpha = 0.1f), RoundedCornerShape(8.dp)).clickable { }.padding(12.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(imageVector = icon, contentDescription = title, tint = Color(0xFF564338), modifier = Modifier.size(18.dp))
        Text(text = title, color = Color(0xFF1B1C1C), fontSize = 14.sp, modifier = Modifier.weight(1f))
        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF564338), modifier = Modifier.size(16.dp))
    }
}