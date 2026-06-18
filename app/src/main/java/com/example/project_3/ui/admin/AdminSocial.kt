package com.example.project_3.ui.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke // THÊM IMPORT NÀY THAY CHO BoxStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.delay

// Đóng gói màu vào Object để tránh xung đột với file AdminAdopt
private object SocialColors {
    val OrangePrimary = Color(0xFF9B4500)
    val PrimaryContainer = Color(0xFFFF8C42)
    val OnPrimaryContainer = Color(0xFF6A2D00)
    val PrimaryFixed = Color(0xFFFFDBC9)
    val OnPrimaryFixedVariant = Color(0xFF763300)
    val TealSecondary = Color(0xFF006A65)
    val BackgroundColor = Color(0xFFFBF9F8)
    val SurfaceContainerLowest = Color(0xFFFFFFFF)
    val SurfaceContainerLow = Color(0xFFF5F3F3)
    val OnSurfaceVariant = Color(0xFF564338)
    val OutlineVariant = Color(0xFFDDC1B3).copy(alpha = 0.4f)
    val ErrorColor = Color(0xFFBA1A1A)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSocial(navController: NavController) {
    var showRejectDialog by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf<String?>(null) }
    var isPost1Visible by remember { mutableStateOf(true) }
    var isPost2Visible by remember { mutableStateOf(true) }

    LaunchedEffect(toastMessage) {
        if (toastMessage != null) {
            delay(3000)
            toastMessage = null
        }
    }

    Scaffold(
        containerColor = SocialColors.BackgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Duyệt bài đăng Social",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = SocialColors.OrangePrimary
                    )
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFF8C42).copy(alpha = 0.4f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Admin Avatar", tint = SocialColors.OrangePrimary, modifier = Modifier.fillMaxSize())
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SocialColors.BackgroundColor)
            )
        },
        bottomBar = {
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

            NavigationBar(
                containerColor = SocialColors.SurfaceContainerLowest,
                tonalElevation = 8.dp
            ) {
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
                            selectedIconColor = SocialColors.OnPrimaryContainer,
                            selectedTextColor = Color(0xFF1B1C1C),
                            indicatorColor = SocialColors.PrimaryContainer.copy(alpha = 0.4f),
                            unselectedIconColor = SocialColors.OnSurfaceVariant,
                            unselectedTextColor = SocialColors.OnSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { paddingValues ->

        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BentoStatCard(title = "CHỜ DUYỆT", value = "12", valueColor = SocialColors.OrangePrimary, modifier = Modifier.weight(1f))
                        BentoStatCard(title = "HÔM NAY", value = "48", valueColor = SocialColors.TealSecondary, modifier = Modifier.weight(1f))
                        BentoStatCard(title = "ĐÁNH GIÁ", value = "92%", valueColor = Color(0xFF3F403A), modifier = Modifier.weight(1f))
                    }
                }

                if (isPost1Visible) {
                    item {
                        PostCard(
                            authorName = "Minh Anh Lê",
                            timeAgo = "15 phút trước",
                            hashtag = "#HappyPaws",
                            content = "Vừa mới đón bé Bông về nhà từ Shelter! Bé rất ngoan và quấn người. Cảm ơn trạm cứu hộ đã chăm sóc bé tận tình...",
                            imageContent = {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(140.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Box(modifier = Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(8.dp)).background(Color.LightGray), contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Image, contentDescription = "Pet 1", tint = Color.Gray)
                                    }
                                    Box(modifier = Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(8.dp)).background(Color.LightGray), contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Image, contentDescription = "Pet 2", tint = Color.Gray)
                                    }
                                }
                            },
                            onApprove = {
                                isPost1Visible = false
                                toastMessage = "Đã duyệt bài viết thành công."
                            },
                            onReject = { showRejectDialog = true }
                        )
                    }
                }

                if (isPost2Visible) {
                    item {
                        PostCard(
                            authorName = "Trần Tuấn",
                            timeAgo = "1 giờ trước",
                            hashtag = "#PetAdoption",
                            content = "Cần tìm chủ mới cho bé mèo này do mình sắp đi du học. Bé cực kỳ quấn người và đã tiêm phòng đầy đủ.",
                            imageContent = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.LightGray),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Image, contentDescription = "Single Pet", tint = Color.Gray)
                                }
                            },
                            onApprove = {
                                isPost2Visible = false
                                toastMessage = "Đã duyệt bài viết thành công."
                            },
                            onReject = { showRejectDialog = true }
                        )
                    }
                }
                item { Spacer(modifier = Modifier.height(24.dp)) }
            }

            AnimatedVisibility(
                visible = toastMessage != null,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1C1C)),
                    shape = CircleShape, // SỬA TỪ 999px
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Success", tint = Color(0xFF7CF6EC))
                        Text(text = toastMessage ?: "", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }

    if (showRejectDialog) {
        var selectedReason by remember { mutableStateOf(0) }
        var otherReasonText by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { showRejectDialog = false }) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SocialColors.SurfaceContainerLowest)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Lý do từ chối", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B1C1C))
                        IconButton(onClick = { showRejectDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Text(text = "Vui lòng chọn hoặc nhập lý do từ chối bài viết này.", fontSize = 13.sp, color = SocialColors.OnSurfaceVariant)

                    ReasonRadioRow(text = "Nội dung không phù hợp", selected = selectedReason == 0, onClick = { selectedReason = 0 })
                    ReasonRadioRow(text = "Hình ảnh chất lượng kém", selected = selectedReason == 1, onClick = { selectedReason = 1 })
                    ReasonRadioRow(text = "Lý do khác...", selected = selectedReason == 2, onClick = { selectedReason = 2 })

                    if (selectedReason == 2) {
                        OutlinedTextField(
                            value = otherReasonText,
                            onValueChange = { otherReasonText = it },
                            placeholder = { Text("Nhập lý do cụ thể...", fontSize = 14.sp) },
                            modifier = Modifier.fillMaxWidth().height(80.dp),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { showRejectDialog = false }) {
                            Text("Hủy", color = SocialColors.OnSurfaceVariant, fontWeight = FontWeight.Medium)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                showRejectDialog = false
                                toastMessage = "Đã từ chối bài viết."
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SocialColors.ErrorColor),
                            shape = CircleShape // SỬA TỪ 999px
                        ) {
                            Text("Từ chối", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BentoStatCard(title: String, value: String, valueColor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.border(1.dp, SocialColors.OutlineVariant, RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(containerColor = SocialColors.SurfaceContainerLowest),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SocialColors.OnSurfaceVariant, letterSpacing = 0.5.sp)
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = valueColor)
        }
    }
}

@Composable
fun PostCard(
    authorName: String,
    timeAgo: String,
    hashtag: String,
    content: String,
    imageContent: @Composable () -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().border(1.dp, SocialColors.OutlineVariant, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = SocialColors.SurfaceContainerLowest),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.LightGray), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Person, contentDescription = "Author Avatar", tint = SocialColors.OnSurfaceVariant)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = authorName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B1C1C))
                    Text(
                        text = buildString {
                            append(timeAgo)
                            append(" • ")
                            append(hashtag)
                        },
                        fontSize = 12.sp,
                        color = SocialColors.OnSurfaceVariant
                    )
                }
                Box(modifier = Modifier.background(SocialColors.PrimaryFixed, CircleShape).padding(horizontal = 10.dp, vertical = 2.dp)) { // SỬA TỪ 999px TRÊN DÒNG NÀY
                    Text(text = "CHỜ", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SocialColors.OnPrimaryFixedVariant)
                }
            }

            Text(text = content, fontSize = 14.sp, color = Color(0xFF1B1C1C), lineHeight = 20.sp)

            imageContent()

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier.weight(1f).height(40.dp),
                    border = BorderStroke(1.dp, SocialColors.ErrorColor), // SỬA BoxStroke THÀNH BorderStroke
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SocialColors.ErrorColor),
                    shape = CircleShape // SỬA TỪ 999px
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Reject Icon", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Từ chối", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Button(
                    onClick = onApprove,
                    modifier = Modifier.weight(1f).height(40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SocialColors.OrangePrimary),
                    shape = CircleShape // SỬA TỪ 999px
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Approve Icon", modifier = Modifier.size(16.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Duyệt", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun ReasonRadioRow(text: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, if (selected) SocialColors.OrangePrimary else SocialColors.OutlineVariant, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = SocialColors.OrangePrimary)
        )
        Text(text = text, fontSize = 14.sp, color = Color(0xFF1B1C1C))
    }
}