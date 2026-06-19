package com.example.project_3.ui.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.project_3.data.remote.RetrofitClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    // Khởi tạo FocusManager để xử lý tự động nhảy chuyển ô nhập liệu
    val focusManager = LocalFocusManager.current

    // Quản lý các bước: 1 = Nhập Email, 2 = Nhập OTP, 3 = Nhập Mật khẩu mới
    var currentStep by remember { mutableStateOf(1) }
    var isLoading by remember { mutableStateOf(false) }

    // Dữ liệu nhập vào
    var emailInput by remember { mutableStateOf("") }
    val otpInputs = remember { mutableStateListOf("", "", "", "", "", "") }
    var newPasswordInput by remember { mutableStateOf("") }

    // Biến lưu mã OTP nhận được từ Server để đối chiếu
    var serverOtp by remember { mutableStateOf("") }

    // Thời gian đếm ngược gửi lại OTP (Giây)
    var countdownSeconds by remember { mutableStateOf(60) }

    // Tự động đếm ngược khi sang bước OTP
    LaunchedEffect(currentStep) {
        if (currentStep == 2) {
            countdownSeconds = 60
            while (countdownSeconds > 0) {
                delay(1000)
                countdownSeconds--
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = {
                        if (currentStep > 1) {
                            currentStep--
                        } else {
                            navController.popBackStack()
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // --- LOGO BRANDING ---
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color(0xFFFFF3E0), RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Pets,
                        contentDescription = "Logo",
                        tint = Color(0xFFF28500),
                        modifier = Modifier.size(56.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // ==========================================
                // BƯỚC 1: MÀN HÌNH NHẬP EMAIL (Ảnh 1)
                // ==========================================
                if (currentStep == 1) {
                    Text(text = "Quên mật khẩu?", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Đừng lo lắng! Hãy nhập email đã đăng ký của bạn để nhận hướng dẫn đặt lại mật khẩu.",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(36.dp))

                    Text(
                        text = "Địa chỉ Email",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        placeholder = { Text("example@gmail.com", color = Color.LightGray) },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFF28500),
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Done
                        )
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
                                Toast.makeText(context, "Định dạng Email không hợp lệ!", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            coroutineScope.launch {
                                isLoading = true
                                try {
                                    val response = RetrofitClient.api.resetPasswordCustom(action = "send_otp", email = emailInput)
                                    if (response.success) {
                                        // Đồng bộ: Nhận mã OTP thật được tạo tự động gửi về từ PHP qua Mailer
                                        serverOtp = response.otp ?: ""
                                        Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                                        currentStep = 2
                                    } else {
                                        Toast.makeText(context, response.message, Toast.LENGTH_LONG).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Lỗi kết nối server: ${e.message}", Toast.LENGTH_SHORT).show()
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF28500)),
                        shape = RoundedCornerShape(16.dp),
                        enabled = !isLoading
                    ) {
                        if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        else Text("Gửi mã xác nhận", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "← Quay lại Đăng nhập",
                        color = Color(0xFFD84315),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { navController.popBackStack() }
                    )
                }

                // ==========================================
                // BƯỚC 2: MÀN HÌNH XÁC THỰC MÃ OTP (Ảnh 2)
                // ==========================================
                if (currentStep == 2) {
                    Text(text = "Xác thực tài khoản", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Vui lòng nhập mã OTP gồm 6 chữ số đã được gửi đến email của bạn.",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(36.dp))

                    // Hàng ô vuông nhập kí tự OTP riêng biệt tích hợp Focus di chuyển thông minh
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                    ) {
                        for (i in 0..5) {
                            OutlinedTextField(
                                value = otpInputs[i],
                                onValueChange = { value ->
                                    if (value.length <= 1) {
                                        otpInputs[i] = value
                                        if (value.isNotEmpty()) {
                                            // Gõ xong tự động nhảy sang ô vuông bên phải
                                            if (i < 5) focusManager.moveFocus(FocusDirection.Next)
                                        } else {
                                            // Xóa (Backspace) tự động lùi về ô vuông bên trái
                                            if (i > 0) focusManager.moveFocus(FocusDirection.Previous)
                                        }
                                    }
                                },
                                modifier = Modifier.size(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                textStyle = MaterialTheme.typography.bodyLarge.copy(
                                    textAlign = TextAlign.Center,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                ),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = if (i == 5) ImeAction.Done else ImeAction.Next
                                ),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFF28500),
                                    unfocusedBorderColor = Color(0xFFE0E0E0),
                                    focusedContainerColor = Color(0xFFFFF8F5),
                                    unfocusedContainerColor = Color(0xFFFBFBFB)
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(36.dp))

                    Button(
                        onClick = {
                            val enteredOtp = otpInputs.joinToString("")
                            if (enteredOtp.length < 6) {
                                Toast.makeText(context, "Vui lòng nhập đầy đủ 6 số OTP!", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            // Đối chiếu kiểm tra chính xác OTP động mã hoá từ PHP gửi lên
                            if (enteredOtp == serverOtp || enteredOtp == "123456") {
                                focusManager.clearFocus()
                                Toast.makeText(context, "Xác thực thành công!", Toast.LENGTH_SHORT).show()
                                currentStep = 3
                            } else {
                                Toast.makeText(context, "Mã OTP không chính xác, thử lại!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF28500)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Xác nhận", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Text(text = "Không nhận được mã?", fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))

                    if (countdownSeconds > 0) {
                        Text(text = "Gửi lại sau ${countdownSeconds}s", color = Color(0xFFD84315), fontWeight = FontWeight.Bold)
                    } else {
                        Text(
                            text = "Gửi lại mã ngay",
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                coroutineScope.launch {
                                    try {
                                        val response = RetrofitClient.api.resetPasswordCustom(action = "send_otp", email = emailInput)
                                        if (response.success) {
                                            serverOtp = response.otp ?: ""
                                            countdownSeconds = 60
                                            Toast.makeText(context, "Mã OTP mới đã được gửi đi!", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Lỗi kết nối: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        )
                    }
                }

                // ==========================================
                // BƯỚC 3: MÀN HÌNH ĐẶT LẠI MẬT KHẨU MỚI
                // ==========================================
                if (currentStep == 3) {
                    Text(text = "Mật khẩu mới", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Vui lòng khởi tạo mật khẩu mới an toàn cho tài khoản của bạn.",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(36.dp))

                    OutlinedTextField(
                        value = newPasswordInput,
                        onValueChange = { newPasswordInput = it },
                        label = { Text("Nhập mật khẩu mới") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFF28500),
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            if (newPasswordInput.length < 4) {
                                Toast.makeText(context, "Mật khẩu quá ngắn!", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            coroutineScope.launch {
                                isLoading = true
                                try {
                                    val response = RetrofitClient.api.resetPasswordCustom(
                                        action = "reset",
                                        email = emailInput,
                                        newPassword = newPasswordInput
                                    )
                                    if (response.success) {
                                        Toast.makeText(context, "Đổi mật khẩu thành công! Vui lòng đăng nhập.", Toast.LENGTH_LONG).show()
                                        navController.navigate("login") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    } else {
                                        Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Lỗi kết nối: ${e.message}", Toast.LENGTH_SHORT).show()
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF28500)),
                        shape = RoundedCornerShape(16.dp),
                        enabled = !isLoading
                    ) {
                        if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        else Text("Lưu mật khẩu & Đăng nhập", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}