package com.example.project_3.ui.adopt

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.project_3.data.local.SessionManager
import com.example.project_3.viewmodel.PetDetailViewModel

// Đảm bảo không bị báo lỗi đỏ icon giới tính
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Female

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PetDetailScreen(
    idPet: Int,
    navController: NavController,
    viewModel: PetDetailViewModel = viewModel()
) {
    val context = LocalContext.current

    // Lấy thông tin user hiện tại từ SessionManager
    val sessionManager = remember { SessionManager(context) }
    val currentUserId = sessionManager.getUserId()

    // Gọi tải dữ liệu khi vào màn hình
    LaunchedEffect(idPet) {
        viewModel.loadPetDetail(idPet)
    }

    val pet = viewModel.petDetail

    if (pet == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFF8D4000))
        }
    } else {
        // Chuẩn hóa đường dẫn ảnh động chuẩn xác khớp với cấu trúc thư mục XAMPP backend
        val fullImageUrl = remember(pet.image) {
            val imagePath = pet.image ?: ""
            when {
                imagePath.startsWith("/images/") -> "http://10.0.2.2/project-3$imagePath"
                imagePath.startsWith("images/") -> "http://10.0.2.2/project-3/$imagePath"
                else -> "http://10.0.2.2/project-3/images/$imagePath"
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(Color.White)
            ) {
                // --- 1. IMAGE HEADER ---
                Box {
                    AsyncImage(
                        model = fullImageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth().height(450.dp),
                        contentScale = ContentScale.Crop
                    )

                    // Nút Back & Tim
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp).statusBarsPadding(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.background(Color.White.copy(alpha = 0.8f), CircleShape)
                        ) { Icon(Icons.Default.ArrowBack, null) }

                        IconButton(
                            onClick = { },
                            modifier = Modifier.background(Color.White.copy(alpha = 0.8f), CircleShape)
                        ) { Icon(Icons.Default.FavoriteBorder, null, tint = Color.Red) }
                    }

                    // Badge Trạng thái
                    Surface(
                        modifier = Modifier.align(Alignment.BottomStart).padding(16.dp),
                        color = Color(0xFF80F3E2),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (pet.state == "available") "Sẵn sàng nhận nuôi" else "Đã được đặt",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontSize = 14.sp, fontWeight = FontWeight.Medium
                        )
                    }
                }

                // --- 2. INFO SECTION ---
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(pet.name_pet, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Pets, null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                                Spacer(Modifier.width(4.dp))
                                val displaySpecies = if(pet.species == "dog") "Chó" else if(pet.species == "cat") "Mèo" else pet.species
                                Text("$displaySpecies • ${pet.age} Tuổi", color = Color.Gray)
                            }
                        }

                        // Gender Box
                        Surface(
                            color = if(pet.gender == "male") Color(0xFFFFE0D6) else Color(0xFFFFD6E0),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if(pet.gender == "male") Icons.Default.Male else Icons.Default.Female,
                                    contentDescription = null,
                                    tint = if(pet.gender == "male") Color(0xFFD84315) else Color.Red
                                )
                                Text(if(pet.gender == "male") " Đực" else " Cái", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                    Text("Giới thiệu", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = pet.description,
                        modifier = Modifier.padding(top = 8.dp),
                        color = Color.Gray,
                        lineHeight = 22.sp
                    )

                    // --- 3. HEALTH CARDS ---
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)) {
                        HealthCard(Modifier.weight(1f), Icons.Default.AssignmentTurnedIn, "Sức khỏe", "Đã triệt sản")
                        Spacer(Modifier.width(12.dp))
                        HealthCard(Modifier.weight(1f), Icons.Default.VerifiedUser, "Tiêm ngừa", "Đầy đủ các mũi")
                    }
                }
                Spacer(Modifier.height(100.dp))
            }

            // --- 4. BOTTOM ACTION BAR ---
            Surface(
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                shadowElevation = 8.dp,
                color = Color.White
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).navigationBarsPadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedIconButton(
                        onClick = { },
                        modifier = Modifier.size(56.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) { Icon(Icons.Default.ChatBubbleOutline, null) }

                    Spacer(Modifier.width(16.dp))

                    // ... (Các đoạn mã giao diện phía trên giữ nguyên không đổi)

                    Button(
                        onClick = {
                            if (currentUserId == -1) {
                                Toast.makeText(context, "Vui lòng đăng nhập để thực hiện đăng ký nhận nuôi!", Toast.LENGTH_SHORT).show()
                            } else if (pet.state != "available") {
                                Toast.makeText(context, "Bé thú cưng này hiện không thể nhận nuôi!", Toast.LENGTH_SHORT).show()
                            } else {
                                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                val todayAsString = sdf.format(Date())

                                // Đóng gói cấu trúc Map động chứa chính xác 2 từ khóa mà backend PHP yêu cầu
                                val requestMap = mapOf(
                                    "id_user" to currentUserId,         // Kiểu Int (Ví dụ: 1)
                                    "id_pet" to pet.id_pet,             // Kiểu Int (Ví dụ: 12)
                                    "user_name" to "User_$currentUserId",
                                    "email" to "user$currentUserId@example.com",
                                    "name_pet" to pet.name_pet,
                                    "species" to (pet.species ?: ""),
                                    "age" to pet.age.toString(),
                                    "adoption_date" to todayAsString,
                                    "state" to "pending"
                                )

                                // Gọi hàm xử lý trung gian qua Gson động mới cập nhật ở trên
                                viewModel.registerAdoptionDynamic(requestMap) { isSuccess, msg ->
                                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                    if (isSuccess) {
                                        navController.popBackStack()
                                    }
                                }
                            }
                        },
                        enabled = !viewModel.isSubmitting,
                        modifier = Modifier.weight(1f).height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8D4000)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (viewModel.isSubmitting) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Icon(Icons.Default.Favorite, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Đăng ký nhận nuôi", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HealthCard(modifier: Modifier, icon: ImageVector, title: String, desc: String) {
    Surface(
        modifier = modifier,
        color = Color(0xFFFBFBFB),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Column(Modifier.padding(16.dp)) {
            Icon(icon, null, tint = Color(0xFF00796B))
            Spacer(Modifier.height(8.dp))
            Text(title, fontWeight = FontWeight.Bold)
            Text(desc, fontSize = 12.sp, color = Color.Gray)
        }
    }
}