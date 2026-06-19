package com.example.project_3.ui.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.project_3.viewmodel.AdminProfileViewModel

// Đồng bộ màu sắc với hệ thống Admin Portal chính
private object EditProfileColors {
    val PrimaryColor = Color(0xFF006F69)
    val SurfaceColor = Color(0xFFFBF9F8)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAdminProfileScreen(
    adminId: String,
    navController: NavController,
    viewModel: AdminProfileViewModel
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val editUiState by viewModel.editUiState.collectAsState()

    // Khởi tạo các biến tạm lưu trữ dữ liệu thay đổi trên Form nhập liệu
    var fullname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var birthday by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Nam") }
    var address by remember { mutableStateOf("") }
    var avatarUrl by remember { mutableStateOf("") }

    // Đổ dữ liệu hiện tại từ profile lên form khi màn hình được tải xong
    LaunchedEffect(uiState.adminInfo) {
        uiState.adminInfo?.let { admin ->
            fullname = admin.fullname ?: ""
            email = admin.email ?: ""
            phone = admin.phone ?: ""
            birthday = admin.birthday ?: ""
            gender = admin.gender ?: "Nam"
            address = admin.address ?: ""
            avatarUrl = admin.avatar ?: ""
        }
    }

    // Xử lý sự kiện khi cập nhật thành công -> Quay lại màn hình trước đó
    LaunchedEffect(editUiState.isUpdateSuccess) {
        if (editUiState.isUpdateSuccess) {
            viewModel.resetUpdateStatus()
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile", fontWeight = FontWeight.Bold, color = EditProfileColors.PrimaryColor) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = EditProfileColors.PrimaryColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = EditProfileColors.SurfaceColor)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(EditProfileColors.SurfaceColor)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Account Information",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = EditProfileColors.PrimaryColor
                )

                // --- Khu vực Form điền dữ liệu cấu trúc ---
                OutlinedTextField(
                    value = fullname,
                    onValueChange = { fullname = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = birthday,
                    onValueChange = { birthday = it },
                    label = { Text("Birthday (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // Trường chọn Giới tính đơn giản dạng nút chuyển đổi
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Gender:", fontWeight = FontWeight.SemiBold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = gender == "Nam", onClick = { gender = "Nam" })
                        Text("Nam")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = gender == "Nu", onClick = { gender = "Nu" })
                        Text("Nữ")
                    }
                }

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = avatarUrl,
                    onValueChange = { avatarUrl = it },
                    label = { Text("Avatar Link URL") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                if (editUiState.errorMessage != null) {
                    Text(text = editUiState.errorMessage ?: "", color = Color.Red, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Nút bấm lưu cập nhật
                Button(
                    onClick = {
                        if (fullname.isBlank() || email.isBlank()) {
                            Toast.makeText(context, "Full Name và Email không được để trống", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.updateAdminProfile(
                                adminId = adminId,
                                fullname = fullname,
                                phone = phone,
                                birthday = birthday,
                                gender = gender,
                                address = address,
                                avatar = avatarUrl, // Vẫn truyền vào, ViewModel sẽ xử lý lo liệu phần còn lại
                                email = email
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EditProfileColors.PrimaryColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (editUiState.isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Save Changes", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}