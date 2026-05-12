package com.example.project_3.ui.auth

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation // Fix lỗi VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project_3.R // Đảm bảo đúng package name dự án của bạn

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(onNavigateToLogin: () -> Unit) {
    // State cho các ô nhập liệu
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isAcceptedTerms by remember { mutableStateOf(false) }

    // Sử dụng Column với verticalScroll để hỗ trợ cuộn màn hình khi bàn phím hiện lên
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // Phần Header màu hồng nhạt với Icon dấu chân
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(Color(0xFFFDECE3)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color(0xFF8D4000).copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_paw_print),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Tham gia cộng đồng Paws & Hearts",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Cùng chúng tôi lan tỏa tình yêu thương đến những người bạn bốn chân.",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
            )

            // --- Các ô nhập liệu ---
            RegisterInput(label = "Họ tên", value = fullName, onValueChange = { fullName = it }, placeholder = "Nhập họ và tên", icon = R.drawable.ic_user)
            RegisterInput(label = "Email", value = email, onValueChange = { email = it }, placeholder = "example@gmail.com", icon = R.drawable.ic_email)
            RegisterInput(label = "Mật khẩu", value = password, onValueChange = { password = it }, placeholder = "••••••••", icon = R.drawable.ic_lock, isPassword = true)
            RegisterInput(label = "Xác nhận", value = confirmPassword, onValueChange = { confirmPassword = it }, placeholder = "••••••••", icon = R.drawable.ic_lock_check, isPassword = true)

            // --- Checkbox Điều khoản ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isAcceptedTerms,
                    onCheckedChange = { isAcceptedTerms = it },
                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF8D4000))
                )
                Text(
                    text = "Tôi đồng ý với Điều khoản và Chính sách bảo mật.",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            // --- Nút Đăng ký ---
            Button(
                onClick = { /* Xử lý logic gọi API register.php tại đây */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8D4000))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("ĐĂNG KÝ", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Dòng chuyển sang Đăng nhập ---
            Row(modifier = Modifier.padding(bottom = 32.dp)) {
                Text("Đã có tài khoản? ", color = Color.Gray)
                Text(
                    "Đăng nhập",
                    color = Color(0xFF8D4000),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: Int,
    isPassword: Boolean = false
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.DarkGray)
        Spacer(modifier = Modifier.height(4.dp))

        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, fontSize = 14.sp) },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.Gray
                )
            },
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)),
            // Fix lỗi textFieldColors tại đây cho Material 3
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF5F5F5),
                unfocusedContainerColor = Color(0xFFF5F5F5),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color(0xFF8D4000)
            )
        )
    }
}