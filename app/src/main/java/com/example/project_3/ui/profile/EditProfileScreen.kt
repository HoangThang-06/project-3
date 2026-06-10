package com.example.project_3.ui.profile

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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.project_3.data.local.SessionManager
import com.example.project_3.viewmodel.EditProfileViewModel
import com.example.project_3.viewmodel.factory.ProfileViewModelFactory
import kotlinx.coroutines.delay // IMPORT THÊM ĐOẠN NÀY

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    viewModel: EditProfileViewModel = viewModel(factory = ProfileViewModelFactory(SessionManager(LocalContext.current)))
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // SỬA LẠI ĐOẠN NÀY: Quản lý Toast và Tự động quay lại an toàn
    LaunchedEffect(viewModel.updateResult) {
        viewModel.updateResult?.let { message ->
            // 1. Hiển thị thông báo Toast lên màn hình trước
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

            // 2. Nếu thông báo chứa chữ thành công, đợi 1 giây cho người dùng nhìn thấy rồi quay về
            if (message == "Cập nhật thành công!") {
                delay(1000) // Đợi 1000ms (1 giây)
                navController.popBackStack() // Quay về màn hình ProfileScreen cũ
            }

            // 3. Reset lại trạng thái để tránh lặp lại Toast khi recompose
            viewModel.updateResult = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chỉnh sửa thông tin", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFFBFBFB)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // 1. Ô nhập Họ và Tên
                OutlinedTextField(
                    value = viewModel.fullname,
                    onValueChange = { viewModel.fullname = it },
                    label = { Text("Họ và tên") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !viewModel.isLoading // Khóa khi đang load
                )

                // 2. Ô nhập Email
                OutlinedTextField(
                    value = viewModel.email,
                    onValueChange = { viewModel.email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !viewModel.isLoading
                )

                // 3. Ô nhập Số điện thoại
                OutlinedTextField(
                    value = viewModel.phone,
                    onValueChange = { viewModel.phone = it },
                    label = { Text("Số điện thoại") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !viewModel.isLoading
                )

                // 4. Ô nhập Ngày sinh (Định dạng YYYY-MM-DD)
                OutlinedTextField(
                    value = viewModel.birthday,
                    onValueChange = { viewModel.birthday = it },
                    label = { Text("Ngày sinh (Năm-Tháng-Ngày)") },
                    placeholder = { Text("Ví dụ: 2002-05-15") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !viewModel.isLoading
                )

                // 5. Ô nhập Địa chỉ
                OutlinedTextField(
                    value = viewModel.address,
                    onValueChange = { viewModel.address = it },
                    label = { Text("Địa chỉ") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !viewModel.isLoading
                )

                // 6. Chọn Giới tính (Nam / Nữ)
                Text("Giới tính", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = viewModel.gender == "Nam",
                        onClick = { if (!viewModel.isLoading) viewModel.gender = "Nam" },
                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFFD8C45)),
                        enabled = !viewModel.isLoading
                    )
                    Text("Nam", modifier = Modifier.padding(end = 24.dp))

                    RadioButton(
                        selected = viewModel.gender == "Nu" || viewModel.gender == "Nữ",
                        onClick = { if (!viewModel.isLoading) viewModel.gender = "Nu" },
                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFFD8C45)),
                        enabled = !viewModel.isLoading
                    )
                    Text("Nữ")
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 7. Nút Lưu thay đổi (SỬA LẠI CHỖ CLICK)
                Button(
                    onClick = {
                        // SỬA TẠI ĐÂY: Chỉ cần gọi hàm, việc quay lại màn hình cũ đã có LaunchedEffect ở trên lo liệu
                        viewModel.updateProfile {
                            // Bạn có thể để trống hoặc viết Log debug ở đây
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFD8C45)),
                    enabled = !viewModel.isLoading // Ngăn bấm liên tiếp khi đang chạy API
                ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Lưu thay đổi", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}