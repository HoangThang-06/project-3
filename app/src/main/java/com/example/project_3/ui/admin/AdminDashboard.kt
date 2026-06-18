package com.example.project_3.ui.admin

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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

val SecondaryColor = Color(0xFF006A65)
val SecondaryContainer = Color(0xFF79F3EA)
val SurfaceContainer = Color(0xFFEFEDED)
val ErrorColor = Color(0xFFBA1A1A)
val ErrorContainer = Color(0xFFFFDAD6)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboard(
    navController: NavController
) {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Paws & Hearts",
                        color = PrimaryColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                actions = {
                    // Nút thông báo kèm chấm đỏ nhỏ
                    Box(modifier = Modifier.padding(end = 8.dp)) {
                        IconButton(onClick = { /* Handle Notification click */ }) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Notifications",
                                tint = Color(0xFF1B1C1C)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(ErrorColor, CircleShape)
                                .border(2.dp, BackgroundColor, CircleShape)
                                .align(Alignment.TopEnd)
                                .offset(x = (-8).dp, y = 8.dp)
                        )
                    }
                    // Avatar Admin
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(32.dp)
                            .background(PrimaryContainer, CircleShape)
                    ) {
                        // Thay thế bằng AsyncImage nếu bạn dùng Coil để tải ảnh từ URL
                        Box(modifier = Modifier.fillMaxSize().background(Color.Gray))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundColor)
            )
        },
        bottomBar = {

            val currentRoute =
                navController.currentBackStackEntryAsState().value?.destination?.route

            NavigationBar(
                containerColor = SurfaceContainerLowest,
                tonalElevation = 8.dp
            ) {

                data class BottomItem(
                    val title: String,
                    val route: String,
                    val icon: ImageVector
                )

                val items = listOf(
                    BottomItem(
                        title = "Dash",
                        route = "admin_home",
                        icon = Icons.Default.Home
                    ),
                    BottomItem(
                        title = "Pets",
                        route = "admin_manage_pet",
                        icon = Icons.Default.Pets
                    ),
                    BottomItem(
                        title = "Apps",
                        route = "admin_adopt",
                        icon = Icons.Default.Menu
                    ),
                    BottomItem(
                        title = "Social",
                        route = "admin_social",
                        icon = Icons.Default.Share
                    )
                )

                items.forEach { item ->

                    NavigationBarItem(
                        selected = currentRoute == item.route,

                        onClick = {

                            if (currentRoute != item.route) {

                                navController.navigate(item.route) {

                                    // tránh tạo nhiều màn hình trùng nhau
                                    launchSingleTop = true

                                    // giữ trạng thái màn hình
                                    restoreState = true

                                    // quay về root
                                    popUpTo(
                                        navController.graph.startDestinationId
                                    ) {
                                        saveState = true
                                    }
                                }
                            }
                        },

                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title
                            )
                        },

                        label = {
                            Text(
                                text = item.title,
                                fontSize = 10.sp
                            )
                        },

                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = OnPrimaryContainer,
                            selectedTextColor = Color(0xFF1B1C1C),
                            indicatorColor = PrimaryContainer.copy(alpha = 0.4f),
                            unselectedIconColor = Color(0xFF564338),
                            unselectedTextColor = Color(0xFF564338)
                        )
                    )
                }
            }
        },
        containerColor = BackgroundColor
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

            // 1. Compact Statistics Grid (2 cột x 2 hàng)
            GridStatisticsSection()

            // 2. Progress Indicator (Monthly Adoption Goal)
            AdoptionGoalProgress()

            // 3. Recent Activities Section
            RecentActivitiesSection()

            // 4. Pending Tasks Overview
            PendingTasksSection()

            // 5. Featured Highlight Card
            FeaturedHighlightCard()

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun GridStatisticsSection() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Total Pets",
                value = "1,284",
                badgeText = "+12%",
                icon = Icons.Default.Pets,
                iconBgColor = Color(0xFFFFDBC9),
                iconTintColor = PrimaryColor
            )
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Adoptions",
                value = "856",
                badgeText = "+5%",
                icon = Icons.Default.Favorite,
                iconBgColor = SecondaryContainer,
                iconTintColor = SecondaryColor
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Active Rescues",
                value = "18",
                badgeText = "Urgent",
                icon = Icons.Default.Warning,
                iconBgColor = ErrorContainer,
                iconTintColor = ErrorColor,
                isUrgentBadge = true
            )
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Fund Balance",
                value = "$24.5k",
                badgeText = "",
                icon = Icons.Default.VolunteerActivism,
                iconBgColor = Color(0xFFE4E3DB),
                iconTintColor = Color(0xFF5F5F59)
            )
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    badgeText: String,
    icon: ImageVector,
    iconBgColor: Color,
    iconTintColor: Color,
    isUrgentBadge: Boolean = false,
    modifier1: Modifier = Modifier
) {
    Card(
        modifier = modifier1.then(modifier),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(iconBgColor, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = title, tint = iconTintColor, modifier = Modifier.size(18.dp))
                }
                if (badgeText.isNotEmpty()) {
                    if (isUrgentBadge) {
                        Box(
                            modifier = Modifier
                                .background(ErrorColor.copy(alpha = 0.1f), CircleShape)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(text = badgeText, color = ErrorColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Text(text = badgeText, color = SecondaryColor, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, color = Color(0xFF564338), fontSize = 11.sp, fontWeight = FontWeight.Medium)
            Text(text = value, color = Color(0xFF1B1C1C), fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AdoptionGoalProgress() {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(text = "Monthly Adoption Goal", color = Color(0xFF1B1C1C), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Text(text = "84%", color = PrimaryColor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { 0.84f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = PrimaryContainer,
                trackColor = SurfaceContainer,
            )
        }
    }
}

@Composable
fun RecentActivitiesSection() {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Recent Activities", color = Color(0xFF1B1C1C), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Icon(imageVector = Icons.Default.History, contentDescription = "History", tint = Color(0xFF564338), modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))

            // Item 1: Milo approved
            ActivityItem(
                boldText = "Milo's ",
                normalText = "application approved",
                timeText = "2m",
                icon = Icons.Default.AssignmentTurnedIn,
                iconBg = SecondaryContainer,
                iconTint = SecondaryColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Item 2: Oakwood rescue
            ActivityItem(
                boldText = "Team sent to ",
                normalText = "Oakwood",
                timeText = "15m",
                icon = Icons.Default.LocationOn,
                iconBg = ErrorContainer,
                iconTint = ErrorColor,
                isBoldFirst = false
            )
        }
    }
}

@Composable
fun ActivityItem(
    boldText: String,
    normalText: String,
    timeText: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    isBoldFirst: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(iconBg, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
        }

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
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
                fontSize = 14.sp,
                color = Color(0xFF1B1C1C),
                modifier = Modifier.weight(1f),
                lineHeight = 18.sp
            )
            Text(
                text = timeText,
                fontSize = 10.sp,
                color = Color(0xFF564338),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun PendingTasksSection() {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PENDING TASKS",
                    color = Color(0xFF1B1C1C),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .background(PrimaryColor, CircleShape)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
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
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceContainerLowest, RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFFDDC1B3).copy(alpha = 0.1f), RoundedCornerShape(8.dp))
            .clickable { /* Handle Task click */ }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(imageVector = icon, contentDescription = title, tint = Color(0xFF564338), modifier = Modifier.size(18.dp))
        Text(text = title, color = Color(0xFF1B1C1C), fontSize = 14.sp, modifier = Modifier.weight(1f))
        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF564338), modifier = Modifier.size(16.dp))
    }
}

@Composable
fun FeaturedHighlightCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = PrimaryContainer),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Shelter Hero: Luna",
                    color = OnPrimaryContainer,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Waiting 145 days. Feature her this weekend.",
                    color = OnPrimaryContainer.copy(alpha = 0.9f),
                    fontSize = 13.sp,
                    lineHeight = 16.sp
                )
            }
            Button(
                onClick = { /* Handle Spotlight action */ },
                colors = ButtonDefaults.buttonColors(containerColor = OnPrimaryContainer),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(text = "Spotlight", color = PrimaryContainer, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}