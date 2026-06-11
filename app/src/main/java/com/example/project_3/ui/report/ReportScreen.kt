package com.example.project_3.ui.report

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.project_3.data.local.SessionManager
import com.example.project_3.viewmodel.ReportViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    reportViewModel: ReportViewModel = viewModel(),
    onReportSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val currentUserId = sessionManager.getUserId()

    // Quản lý trạng thái nhập liệu form
    var description by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var imageBytes by remember { mutableStateOf<ByteArray?>(null) }
    var imageFileName by remember { mutableStateOf<String?>(null) }

    // Quản lý trạng thái sức khỏe của Pet
    val petStatusOptions = listOf("Bị thương", "Đói khát", "Đi lạc", "Bị bỏ rơi")
    var selectedStatus by remember { mutableStateOf("Đi lạc") }

    val currentAddress = reportViewModel.currentAddress.value
    val isSubmitting = reportViewModel.isSubmitting.value
    val lat = reportViewModel.latitude.value
    val lng = reportViewModel.longitude.value

    // Khối xin quyền định vị GPS từ điện thoại
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (fineGranted || coarseGranted) {
            reportViewModel.fetchCurrentLocation(context)
        } else {
            reportViewModel.currentAddress.value = "Chưa được cấp quyền định vị vị trí"
        }
    }

    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    // Bộ chọn ảnh từ máy điện thoại
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            imageFileName = "report_${System.currentTimeMillis()}.jpg"
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    imageBytes = inputStream.readBytes()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Không thể đọc dữ liệu ảnh", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Sử dụng Box trơn bọc ngoài cùng giúp đồng bộ giao diện, tránh lặp lại thanh Toolbar hệ thống
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            // TIÊU ĐỀ CHÍNH MÀN HÌNH BÁO CÁO
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Báo Cáo Cứu Hộ",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF8B4513)
                )

                Surface(
                    color = Color(0xFF7FFFD4),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "Khẩn cấp",
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        color = Color(0xFF008B8B),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // KHU VỰC THÊM ẢNH BÁO CÁO
            Text(
                text = "Hình ảnh hiện trường",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .border(width = 1.dp, color = Color(0xFFD3D3D3), shape = RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Thêm ảnh", color = Color.Gray, fontSize = 12.sp)
                    }
                }

                if (selectedImageUri != null) {
                    Box(modifier = Modifier.size(90.dp)) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .background(Color.Red, CircleShape)
                                .align(Alignment.TopEnd)
                                .offset(x = 6.dp, y = (-6).dp)
                                .clickable {
                                    selectedImageUri = null
                                    imageBytes = null
                                    imageFileName = null
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // KHU VỰC CHỌN TRẠNG THÁI CỦA PET
            Text(
                text = "Trạng thái của thú cưng",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                petStatusOptions.forEach { status ->
                    val isSelected = selectedStatus == status
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedStatus = status },
                        label = { Text(status, fontSize = 13.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF8B4513),
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFFF5F5F5),
                            labelColor = Color.Gray
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // MÔ TẢ TÌNH TRẠNG CHI TIẾT
            Text(
                text = "Mô tả tình trạng chi tiết",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = {
                    Text("Nhập đặc điểm nhận dạng, màu lông, mức độ nguy hiểm...", color = Color.LightGray, fontSize = 13.sp)
                },
                modifier = Modifier.fillMaxWidth().height(110.dp),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF8B4513),
                    unfocusedBorderColor = Color(0xFFE0E0E0)
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // KHỐI THÔNG TIN VỊ TRÍ TỰ ĐỘNG LẤY TỪ GPS
            Text(
                text = "Vị trí cứu trợ (Tự động xác định)",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F7F7)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(40.dp).background(Color(0xFF007A7A), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (lat != 0.0 && lng != 0.0) "Tọa độ: $lat, $lng" else "Đang quét GPS...",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF005A5A),
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentAddress,
                            color = Color(0xFF007A7A),
                            fontSize = 13.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Nút cập nhật lại tọa độ thủ công
                    IconButton(
                        onClick = { reportViewModel.fetchCurrentLocation(context) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Quét lại",
                            tint = Color(0xFF007A7A),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // NÚT BẤM ĐĂNG BÁO CÁO LÊN SERVER MYSQL
            Button(
                onClick = {
                    if (currentUserId == -1) {
                        Toast.makeText(context, "Vui lòng đăng nhập trước!", Toast.LENGTH_SHORT).show()
                    } else {
                        // Gọi trực tiếp dữ liệu thô và trạng thái độc lập sang ViewModel
                        reportViewModel.submitReport(
                            userId = currentUserId,
                            description = description.trim(),
                            status = selectedStatus, // Truyền trực tiếp trạng thái được chọn xuống database
                            imageBytes = imageBytes,
                            imageFileName = imageFileName,
                            onSuccess = {
                                Toast.makeText(context, "Đã gửi cứu hộ thành công!", Toast.LENGTH_LONG).show()
                                description = ""
                                selectedImageUri = null
                                imageBytes = null
                                onReportSuccess()
                            },
                            onFailure = { errorMessage ->
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B4513)),
                shape = RoundedCornerShape(26.dp),
                enabled = !isSubmitting && description.trim().isNotEmpty()
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "Gửi báo cáo ngay", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}