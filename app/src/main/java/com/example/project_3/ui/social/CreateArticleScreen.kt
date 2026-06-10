package com.example.project_3.ui.social

import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.project_3.R
import com.example.project_3.viewmodel.SocialViewModel
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateArticleScreen(
    socialViewModel: SocialViewModel,
    currentUserId: Int,
    onBack: () -> Unit // Điều hướng quay lại màn hình mạng xã hội khi lưu xong
) {
    val context = LocalContext.current
    var contentText by remember { mutableStateOf("") }
    var hashtagText by remember { mutableStateOf("") }

    // Biến lưu giữ URI của hình ảnh vừa chọn trong máy
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val isPublishing = socialViewModel.isPublishing.value

    // Bộ khởi tạo lắng nghe sự kiện chọn ảnh từ máy của Android Hệ thống
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tạo bài viết", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // NÚT ĐĂNG BÀI VIẾT VỊ TRÍ GÓC PHẢI TRÊN
                    if (isPublishing) {
                        CircularProgressIndicator(color = Color(0xFFFD8C45), modifier = Modifier.size(24.dp).padding(end = 16.dp))
                    } else {
                        Button(
                            onClick = {
                                if (currentUserId == -1) {
                                    Toast.makeText(context, "Lỗi: Chưa đăng nhập!", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                // Đọc mảng Bytes và Tên file thực từ Uri ảnh để truyền xuống ViewModel
                                var imageBytes: ByteArray? = null
                                var fileName: String? = null

                                selectedImageUri?.let { uri ->
                                    try {
                                        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                                        imageBytes = inputStream?.readBytes()

                                        // Tìm tên file chính xác
                                        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                                            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                                            if (cursor.moveToFirst()) {
                                                fileName = cursor.getString(nameIndex)
                                            }
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }

                                socialViewModel.uploadArticle(
                                    userId = currentUserId,
                                    content = contentText,
                                    category = hashtagText,
                                    imageBytes = imageBytes,
                                    imageFileName = fileName ?: "article_image.jpg",
                                    onSuccess = {
                                        Toast.makeText(context, "Đăng bài viết thành công!", Toast.LENGTH_SHORT).show()
                                        onBack() // Quay lại trang dòng thời gian
                                    },
                                    onFailure = { errorMessage ->
                                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                                    }
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFD8C45)),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.padding(end = 8.dp),
                            enabled = contentText.trim().isNotEmpty()
                        ) {
                            Text("Đăng", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // 1. KHU VỰC NHẬP NỘI DUNG CHỮ
            TextField(
                value = contentText,
                onValueChange = { contentText = it },
                placeholder = { Text("Bạn đang nghĩ gì thế?...", color = Color.Gray, fontSize = 16.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = LocalTextStyle.current.copy(fontSize = 16.sp)
            )

            // 2. KHU VỰC NHẬP HASHTAGS
            TextField(
                value = hashtagText,
                onValueChange = { hashtagText = it },
                placeholder = { Text("Ví dụ: #thucung #vuive", color = Color.LightGray, fontSize = 14.sp) },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                leadingIcon = { Text("#", fontWeight = FontWeight.Bold, color = Color.Gray) }
            )

            // 3. KHU VỰC HIỂN THỊ ẢNH XEM TRƯỚC (PREVIEW) HOẶC Ô KHUNG TRỐNG ĐỂ CHỌN
            if (selectedImageUri != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(16.dp))
                ) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Preview Image",
                        modifier = Modifier.fillMaxSize().border(1.dp, Color.Transparent, RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                    // Nút Xóa ảnh đã lựa chọn ở góc phải ảnh
                    IconButton(
                        onClick = { selectedImageUri = null },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(28.dp),
                        colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Black.copy(alpha = 0.6f))
                    ) {
                        Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            } else {
                // Ô khung trống kích thích người dùng bấm vào chọn ảnh
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .border(2.dp, Color(0xFFEEEEEE), RoundedCornerShape(16.dp))
                        .clickable { imagePickerLauncher.launch("image/*") }, // Mở thư viện chọn ảnh trong điện thoại
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(painterResource(id = R.drawable.ic_image), null, tint = Color.Gray, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Thêm hình ảnh vào bài viết", color = Color.Gray, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}