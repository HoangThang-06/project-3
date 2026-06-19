package com.example.project_3.ui.admin

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.SubcomposeAsyncImage
import com.example.project_3.data.model.Article
import com.example.project_3.viewmodel.AdminManageArticleViewModel

// Định nghĩa màu sắc Token UI đồng bộ hệ thống Admin
val AdminSecondaryColor = Color(0xFF006A65)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSocial(
    navController: NavController,
    viewModel: AdminManageArticleViewModel,
    onEditArticleClick: (Article) -> Unit,
    onAddArticleClick: () -> Unit
) {
    val context = LocalContext.current
    val articles = viewModel.uiState.collectAsState().value.articles
    val isLoading = viewModel.uiState.collectAsState().value.isLoading
    val messageNotification by viewModel.messageNotification

    var selectedFilter by remember { mutableStateOf("Tất cả") }

    LaunchedEffect(messageNotification) {
        messageNotification?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearNotification()
        }
    }

    val filteredArticles = remember(articles, selectedFilter) {
        if (selectedFilter == "Tất cả") {
            articles
        } else {
            articles.filter { it.category.contains(selectedFilter, ignoreCase = true) }
        }
    }

    Scaffold(
        containerColor = BackgroundColor,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundColor),
                title = { Text("Paws & Hearts Admin", color = PrimaryColor, fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { }) { Icon(Icons.Default.Notifications, contentDescription = null, tint = OnSurfaceVariant) }
                    Box(modifier = Modifier.size(32.dp).background(PrimaryFixed, CircleShape).clip(CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryColor)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                }
            )
        },
        bottomBar = {
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
            NavigationBar(containerColor = SurfaceContainerLowest, tonalElevation = 8.dp) {
                val items = listOf(
                    Triple("Dash", "admin_home", Icons.Default.Home),
                    Triple("Pets", "admin_manage_pet", Icons.Default.Pets),
                    Triple("Apps", "admin_adopt", Icons.Default.Menu),
                    Triple("Social", "admin_social", Icons.Default.Share),
                    Triple("Users", "admin_manage_user", Icons.Default.People) // Thêm ở đây
                )
                items.forEach { (title, route, icon) ->
                    NavigationBarItem(
                        selected = currentRoute?.startsWith(route) == true,
                        onClick = {
                            if (currentRoute != route) {
                                navController.navigate(route) {
                                    launchSingleTop = true
                                    restoreState = true
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                }
                            }
                        },
                        icon = { Icon(icon, contentDescription = title) },
                        label = { Text(title, fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = OnPrimaryContainer,
                            selectedTextColor = Color(0xFF1B1C1C),
                            indicatorColor = PrimaryContainer.copy(alpha = 0.4f),
                            unselectedIconColor = OnSurfaceVariant,
                            unselectedTextColor = OnSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryColor)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Quản lý bài viết", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                            Text("Cập nhật kiến thức nuôi thú cưng.", fontSize = 14.sp, color = OnSurfaceVariant)
                        }
                        Button(
                            onClick = onAddArticleClick,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Article", tint = Color.White)
                        }
                    }
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatCard(title = "Tổng bài viết", value = "${articles.size}", color = PrimaryColor, modifier = Modifier.weight(1f))
                        StatCard(title = "Lượt xem", value = "${articles.sumOf { it.click }}", color = AdminSecondaryColor, modifier = Modifier.weight(1f))
                    }
                }

                item {
                    val filterOptions = listOf("Tất cả", "Chó", "Mèo")
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        items(filterOptions) { option ->
                            val isSelected = selectedFilter == option
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedFilter = option },
                                label = { Text(option) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PrimaryColor,
                                    selectedLabelColor = Color.White,
                                    containerColor = SurfaceContainerHigh
                                ),
                                border = null,
                                shape = RoundedCornerShape(50.dp)
                            )
                        }
                    }
                }

                if (filteredArticles.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp), contentAlignment = Alignment.Center) {
                            Text("Không có bài viết nào.", color = OnSurfaceVariant, fontSize = 16.sp)
                        }
                    }
                } else {
                    items(filteredArticles, key = { it.id_article }) { article ->
                        ArticleRowCard(
                            article = article,
                            onEdit = {
                                viewModel.selectArticleForEdit(article)
                                onEditArticleClick(article)
                            },
                            onToggleStatus = {
                                // ĐÃ SỬA: Gọi đúng hàm updateArticleStatus với 2 tham số khớp với ViewModel của bạn
                                viewModel.updateArticleStatus(article.id_article, article.status)
                            },
                            onDelete = {
                                viewModel.deleteArticle(article.id_article)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(SurfaceContainerLowest, RoundedCornerShape(16.dp))
            .border(1.dp, SurfaceContainerHigh, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(title.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = OnSurfaceVariant, letterSpacing = 0.5.sp)
        Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color, modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
fun ArticleRowCard(
    article: Article,
    onEdit: () -> Unit,
    onToggleStatus: () -> Unit,
    onDelete: () -> Unit
) {
    val isPrivate = article.status == "private"

    val fullImageUrl = if (article.image.startsWith("images/") || article.image.startsWith("http")) {
        article.image
    } else {
        "${BASE_SERVER_URL}images/${article.image}"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .background(
                if (isPrivate) SurfaceContainerHigh.copy(alpha = 0.6f) else SurfaceContainerLowest,
                RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.dp,
                color = if (isPrivate) OnSurfaceVariant.copy(alpha = 0.4f) else SurfaceContainerHigh,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onEdit() }
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .background(SurfaceContainerLow, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
        ) {
            SubcomposeAsyncImage(
                model = fullImageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                loading = {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PrimaryColor, strokeWidth = 2.dp, modifier = Modifier.size(24.dp))
                    }
                },
                error = {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(36.dp), tint = OnSurfaceVariant.copy(alpha = 0.3f))
                    }
                }
            )

            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(4.dp)
                    .background(
                        if (isPrivate) Color(0xFFE2E3E5) else Color(0xFFDFF6DD),
                        RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = article.status.uppercase(),
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isPrivate) Color.DarkGray else AdminSecondaryColor
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .height(96.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = article.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isPrivate) OnSurfaceVariant else OnSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = article.content,
                    fontSize = 13.sp,
                    color = OnSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Red)
                        Text(text = "${article.likes_count}", fontSize = 12.sp, color = OnSurfaceVariant)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(14.dp), tint = AdminSecondaryColor)
                        Text(text = "${article.click} xem", fontSize = 12.sp, color = OnSurfaceVariant)
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Sửa", tint = AdminSecondaryColor, modifier = Modifier.size(20.dp))
                }
                IconButton(onClick = onToggleStatus, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = if (isPrivate) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Trạng thái",
                        tint = if (isPrivate) PrimaryColor else OnSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = Color.Red, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}