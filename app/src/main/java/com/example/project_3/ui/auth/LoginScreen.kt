package com.example.project_3.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.project_3.R
import com.example.project_3.data.local.SessionManager
import com.example.project_3.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = viewModel(),
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {

    // =========================
    // INPUT STATE
    // =========================
    var emailOrUser by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // =========================
    // SESSION
    // =========================
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    // =========================
    // LOGIN SUCCESS (SỬA TẠI ĐÂY)
    // =========================
    LaunchedEffect(loginViewModel.currentUser) {
        loginViewModel.currentUser?.let { user ->

            // THAY THẾ: Thay vì chỉ saveUserId, ta lưu toàn bộ Object User bao gồm cả email, fullname, vv.
            // Hàm này sẽ tự động lưu cả "user_json" và "user_id" theo cấu trúc mới của SessionManager
            sessionManager.saveUser(user)

            // Chuyển màn hình sang trang chủ
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // =========================
        // HEADER
        // =========================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(Color(0xFFFDECE3)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_paws_hearts),
                contentDescription = "Logo",
                modifier = Modifier.size(150.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Chào mừng quay trở lại!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF8D4000),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Hãy tiếp tục hành trình tìm kiếm người bạn bốn chân của bạn.",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // =========================
            // USERNAME
            // =========================
            Text(
                text = "Email hoặc Tên đăng nhập",
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )

            TextField(
                value = emailOrUser,
                onValueChange = { emailOrUser = it },
                placeholder = { Text("@example@gmail.com") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(12.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF5F5F5),
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // =========================
            // PASSWORD
            // =========================
            Text(
                text = "Mật khẩu",
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )

            TextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("••••••••") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(12.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF5F5F5),
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Text(
                text = "Quên mật khẩu?",
                color = Color(0xFF00796B),
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable { },
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // =========================
            // MESSAGE
            // =========================
            if (loginViewModel.message.isNotEmpty()) {
                Text(
                    text = loginViewModel.message,
                    color = if (loginViewModel.message.contains("Success", ignoreCase = true)) {
                        Color(0xFF00796B)
                    } else {
                        Color.Red
                    },
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            // =========================
            // LOGIN BUTTON
            // =========================
            Button(
                onClick = { loginViewModel.login(emailOrUser, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9E4900))
            ) {
                Text(
                    "Đăng nhập",
                    fontSize = 18.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // =========================
            // REGISTER
            // =========================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Chưa có tài khoản? ",
                    color = Color.Gray,
                    fontSize = 14.sp
                )

                Text(
                    text = "Đăng ký tài khoản mới",
                    color = Color(0xFF8D4000),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { onNavigateToRegister() }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}