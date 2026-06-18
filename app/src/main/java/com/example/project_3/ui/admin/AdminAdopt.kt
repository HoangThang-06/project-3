package com.example.project_3.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.project_3.viewmodel.AdminAdoptViewModel

private object AdoptColors {
    val OrangePrimary = Color(0xFF9B4500)
    val PrimaryContainer = Color(0xFFFF8C42)
    val OnPrimaryContainer = Color(0xFF6A2D00)
    val BackgroundColor = Color(0xFFFBF9F8)
    val SurfaceContainerLowest = Color(0xFFFFFFFF)
    val SurfaceContainerLow = Color(0xFFF5F3F3)
    val SurfaceContainer = Color(0xFFEFEDED)
    val OnSurfaceVariant = Color(0xFF564338)
    val SecondaryContainer = Color(0xFF79F3EA)
    val OnSecondaryContainer = Color(0xFF006F69)
    val OutlineVariant = Color(0xFFDDC1B3).copy(alpha = 0.3f)
    val ErrorColor = Color(0xFFBA1A1A)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAdopt(
    navController: NavController,
    viewModel: AdminAdoptViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = AdoptColors.BackgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Duyệt Đơn Nhận Nuôi",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AdoptColors.OrangePrimary
                    )
                },
                actions = {
                    IconButton(onClick = { /* Xử lý thông báo */ }) {
                        Icon(imageVector = Icons.Default.Notifications, contentDescription = "Notifications", tint = AdoptColors.OnSurfaceVariant)
                    }
                    Box(modifier = Modifier.padding(end = 16.dp).size(32.dp).clip(CircleShape).background(Color.LightGray)) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Admin Profile", modifier = Modifier.fillMaxSize())
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AdoptColors.BackgroundColor)
            )
        },
        bottomBar = {
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
            NavigationBar(containerColor = AdoptColors.SurfaceContainerLowest, tonalElevation = 8.dp) {
                val items = listOf(
                    Triple("Dash", "admin_home", Icons.Default.Home),
                    Triple("Pets", "admin_manage_pet", Icons.Default.Pets),
                    Triple("Apps", "admin_adopt", Icons.Default.Menu),
                    Triple("Social", "admin_social", Icons.Default.Share)
                )
                items.forEach { (title, route, icon) ->
                    NavigationBarItem(
                        selected = currentRoute == route,
                        onClick = {
                            if (currentRoute != route) {
                                navController.navigate(route) {
                                    launchSingleTop = true
                                    restoreState = true
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                }
                            }
                        },
                        icon = { Icon(imageVector = icon, contentDescription = title) },
                        label = { Text(text = title, fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AdoptColors.OnPrimaryContainer,
                            selectedTextColor = Color(0xFF1B1C1C),
                            indicatorColor = AdoptColors.PrimaryContainer.copy(alpha = 0.4f),
                            unselectedIconColor = AdoptColors.OnSurfaceVariant,
                            unselectedTextColor = AdoptColors.OnSurfaceVariant
                        )
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.loadAdoptApplications() }, // Dùng nút này làm tính năng làm mới (Refresh) danh sách nhanh
                containerColor = AdoptColors.OrangePrimary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh List")
            }
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                OutlinedTextField(
                    value = uiState.searchText,
                    onValueChange = { viewModel.onSearchTextChanged(it) },
                    placeholder = { Text("Tìm kiếm thú cưng, giống loài...", fontSize = 14.sp) },
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon", tint = Color(0xFF897266)) },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = CircleShape,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = AdoptColors.SurfaceContainer,
                        unfocusedContainerColor = AdoptColors.SurfaceContainer,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "ĐANG CHỜ DUYỆT (${uiState.applications.size})", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B1C1C))
                    TextButton(onClick = { }, contentPadding = PaddingValues(0.dp)) {
                        Text("Sắp xếp ", color = AdoptColors.OrangePrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Icon(Icons.Default.Sort, contentDescription = "Sort", tint = AdoptColors.OrangePrimary, modifier = Modifier.size(14.dp))
                    }
                }
            }

            // XỬ LÝ HIỂN THỊ LOADING TRỰC QUAN
            if (uiState.isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = AdoptColors.OrangePrimary)
                    }
                }
            }

            // XỬ LÝ HIỂN THỊ LỖI KÈM NÚT THỬ LẠI
            if (uiState.errorMessage != null) {
                item {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = uiState.errorMessage!!, color = AdoptColors.ErrorColor, fontSize = 14.sp, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadAdoptApplications() }, colors = ButtonDefaults.buttonColors(containerColor = AdoptColors.OrangePrimary)) {
                            Text("Thử tải lại dữ liệu")
                        }
                    }
                }
            }

            // ĐỔ DANH SÁCH DỮ LIỆU THẬT
            items(uiState.applications, key = { it.id }) { application ->
                ApplicationCard(
                    name = application.applicantName,
                    petName = application.petName,
                    petBreed = application.petBreed,
                    status = application.status,
                    tags = application.tags,
                    note = application.note,
                    actions = {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Button(
                                onClick = { viewModel.rejectApplication(application.id) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = AdoptColors.SurfaceContainerLow),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Reject", tint = AdoptColors.ErrorColor, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Từ chối", color = AdoptColors.ErrorColor, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = { viewModel.approveApplication(application) }, // Đã truyền nguyên Object hợp lệ lên server
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = AdoptColors.OrangePrimary),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = "Approve", tint = Color.White, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Duyệt", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                )
            }

            item {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = AdoptColors.OrangePrimary)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Progress Task", tint = Color.White, modifier = Modifier.size(20.dp))
                                Text("Tiến độ hôm nay", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Text("${(uiState.todayProgress * 100).toInt()}%", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { uiState.todayProgress },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                            color = AdoptColors.SecondaryContainer,
                            trackColor = Color.White.copy(alpha = 0.2f)
                        )
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun ApplicationCard(
    name: String,
    petName: String,
    petBreed: String,
    status: String,
    tags: List<Pair<String, Boolean>>,
    note: String?,
    actions: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().border(1.dp, AdoptColors.OutlineVariant, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AdoptColors.SurfaceContainerLowest)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)).background(Color.LightGray)) {
                    Icon(Icons.Default.Person, contentDescription = "User Avatar", modifier = Modifier.fillMaxSize().padding(12.dp), tint = AdoptColors.OnSurfaceVariant)
                }

                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                        Text(name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B1C1C), modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Box(modifier = Modifier.background(AdoptColors.OrangePrimary.copy(alpha = 0.15f), CircleShape).padding(horizontal = 8.dp, vertical = 2.dp)) {
                            Text(status.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AdoptColors.OrangePrimary)
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.Pets, contentDescription = "Pet", tint = AdoptColors.OrangePrimary, modifier = Modifier.size(12.dp))
                        Text(text = buildString { append(petName); append(" "); append("($petBreed)") }, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = AdoptColors.OrangePrimary)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 4.dp)) {
                        tags.forEach { (tagText, isSpecial) ->
                            val bgTagColor = if (isSpecial) AdoptColors.SecondaryContainer.copy(alpha = 0.3f) else Color(0xFFE4E3DB)
                            val txtTagColor = if (isSpecial) AdoptColors.OnSecondaryContainer else Color(0xFF474742)
                            Box(modifier = Modifier.background(bgTagColor, CircleShape).padding(horizontal = 8.dp, vertical = 2.dp)) {
                                Text(tagText, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = txtTagColor)
                            }
                        }
                    }
                }
            }

            if (note != null) {
                Box(modifier = Modifier.fillMaxWidth().background(AdoptColors.SurfaceContainerLow, RoundedCornerShape(8.dp)).padding(8.dp)) {
                    Text(text = note, fontSize = 14.sp, fontStyle = FontStyle.Italic, color = AdoptColors.OnSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
            }
            actions()
        }
    }
}