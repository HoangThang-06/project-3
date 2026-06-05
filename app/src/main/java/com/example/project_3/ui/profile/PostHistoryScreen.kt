package com.example.project_3.ui.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.project_3.data.local.SessionManager
import com.example.project_3.viewmodel.PostHistoryViewModel
import com.example.project_3.viewmodel.factory.ProfileViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostHistoryScreen(
    navController: NavController,
    viewModel: PostHistoryViewModel = viewModel(factory = ProfileViewModelFactory(SessionManager(LocalContext.current)))
) {
    val context = LocalContext.current
    val postList = viewModel.postList
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage

    // State quản lý việc hiển thị Bottom Sheet
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedPostId by remember { mutableStateOf(-1) }

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Tất cả", "Ảnh & Video", "Tin nhận nuôi")

    LaunchedEffect(errorMessage) {
        errorMessage?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
    }

    // Bộ lọc nội dung dựa trên tab đang chọn
    val filteredPosts = remember(selectedTabIndex, postList) {
        when (selectedTabIndex) {
            1 -> postList.filter { it.category == "Ảnh & Video" }
            2 -> postList.filter { it.category == "Tin nhận nuôi" }
            else -> postList
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
                        Text("Paws & Hearts", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF8D4000))
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
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color(0xFFFD8C45))
            } else {
                Column(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp).verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Lịch sử bài viết", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(16.dp))

                    // --- THANH TAB LỌC (Tất cả, Ảnh & Video, Tin nhận nuôi) ---
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        tabs.forEachIndexed { index, title ->
                            val isSelected = selectedTabIndex == index
                            Box(
                                modifier = Modifier
                                    .weight(if (index == 0) 0.8f else 1.2f)
                                    .height(38.dp)
                                    .clip(RoundedCornerShape(19.dp))
                                    .background(if (isSelected) Color(0xFFFD8C45) else Color(0xFFEEEEEE))
                                    .clickable { selectedTabIndex = index },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isSelected) Color.White else Color.DarkGray
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // --- DANH SÁCH BÀI VIẾT TRÊN CARD TRẮNG ---
                    if (filteredPosts.isEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp), contentAlignment = Alignment.Center) {
                            Text("Không có bài viết nào", color = Color.Gray)
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            filteredPosts.forEach { post ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                ) {
                                    Row(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
                                        // 1. Ảnh bài viết bên trái
                                        AsyncImage(
                                            model = post.imageUrl,
                                            contentDescription = null,
                                            modifier = Modifier.size(90.dp).clip(RoundedCornerShape(12.dp)),
                                            contentScale = ContentScale.Crop
                                        )

                                        Spacer(modifier = Modifier.width(14.dp))

                                        // 2. Nội dung bên phải
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(post.date, fontSize = 13.sp, color = Color.Gray)

                                                // NÚT 3 CHẤM: Khi bấm vào sẽ lưu ID bài viết và mở Bottom Sheet
                                                IconButton(
                                                    onClick = {
                                                        selectedPostId = post.id
                                                        showBottomSheet = true
                                                    },
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Icon(
                                                        Icons.Default.MoreVert,
                                                        contentDescription = null,
                                                        tint = Color.Gray,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(6.dp))

                                            Text(
                                                text = post.content,
                                                fontSize = 14.sp,
                                                color = Color.DarkGray,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )

                                            Spacer(modifier = Modifier.height(12.dp))

                                            // Like, bình luận và Nút sửa
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(Icons.Default.FavoriteBorder, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(post.likes.toString(), fontSize = 12.sp, color = Color.Gray)

                                                    Spacer(modifier = Modifier.width(12.dp))

                                                    Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(post.comments.toString(), fontSize = 12.sp, color = Color.Gray)
                                                }

                                                // Nút sửa viền cam
                                                Box(
                                                    modifier = Modifier
                                                        .border(1.dp, Color(0xFFFD8C45), RoundedCornerShape(12.dp))
                                                        .clickable { /* Điều hướng sang trang sửa bài viết */ }
                                                        .padding(horizontal = 16.dp, vertical = 4.dp)
                                                ) {
                                                    Text("Sửa", fontSize = 13.sp, color = Color(0xFFFD8C45), fontWeight = FontWeight.Medium)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }

        // --- BẢNG ĐIỀU KHIỂN BOTTOM SHEET (ẨN, XÓA, COPY LINK) ---
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                containerColor = Color.White
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp, start = 20.dp, end = 20.dp)) {
                    val clipboardManager = LocalClipboardManager.current

                    // Lấy thông tin bài viết đang được bấm 3 chấm từ danh sách
                    val currentPost = postList.find { it.id == selectedPostId }
                    // Kiểm tra xem bài viết đó có đang bị ẩn hay không
                    val isCurrentlyHidden = currentPost?.status == "private"

                    // 1. Chức năng Sao chép liên kết
                    BottomSheetItem(icon = Icons.Default.Link, title = "Sao chép liên kết") {
                        showBottomSheet = false
                        val linkText = "http://localhost/project-3/get_post_detail.php?id_article=$selectedPostId"
                        clipboardManager.setText(buildAnnotatedString { append(linkText) })
                        Toast.makeText(context, "Đã sao chép liên kết vào bộ nhớ tạm!", Toast.LENGTH_SHORT).show()
                    }

                    // 2. Chức năng Ẩn / Hiện bài viết (ĐÃ SỬA ĐỘNG)
                    val actionIcon = if (isCurrentlyHidden) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    val actionTitle = if (isCurrentlyHidden) "Hiện bài viết" else "Ẩn bài viết"
                    val actionType = if (isCurrentlyHidden) "show" else "hide"

                    BottomSheetItem(icon = actionIcon, title = actionTitle) {
                        showBottomSheet = false
                        viewModel.updatePost(selectedPostId, actionType) { msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }
                    }

                    // 3. Chức năng Xóa bài viết
                    BottomSheetItem(icon = Icons.Default.Delete, title = "Xóa bài viết", isDanger = true) {
                        showBottomSheet = false
                        viewModel.updatePost(selectedPostId, "delete") { msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}

// Composable phụ trợ vẽ các hàng lựa chọn trong Bottom Sheet
@Composable
fun BottomSheetItem(
    icon: ImageVector,
    title: String,
    isDanger: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 14.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isDanger) Color.Red else Color.DarkGray,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = if (isDanger) Color.Red else Color.Black
        )
    }
}