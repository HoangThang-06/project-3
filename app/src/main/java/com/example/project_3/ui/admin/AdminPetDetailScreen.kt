package com.example.project_3.ui.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.project_3.data.model.Pet
import com.example.project_3.viewmodel.AdminManagePetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPetDetailScreen(
    pet: Pet,
    navController: androidx.navigation.NavController,
    viewModel: AdminManagePetViewModel
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Trạng thái bật/tắt chế độ sửa đổi thông tin
    var isEditMode by remember { mutableStateOf(false) }

    // Các biến lưu thông tin chỉnh sửa tạm thời dựa đúng theo Database
    var namePet by remember { mutableStateOf(pet.name_pet) }
    var species by remember { mutableStateOf(pet.species.lowercase()) } // dog, cat, other
    var gender by remember { mutableStateOf(pet.gender.lowercase()) } // male, female
    var age by remember { mutableStateOf(pet.age.toString()) }
    var description by remember { mutableStateOf(pet.description) }
    var statePet by remember { mutableStateOf(pet.state.lowercase()) } // available, reserved, adopted

    // Đường dẫn ảnh đầy đủ kết hợp tiền tố tương tự AdminManagePet.kt
    val fullImageUrl = if (pet.image.startsWith("images/")) {
        "${BASE_SERVER_URL}${pet.image}"
    } else {
        "${BASE_SERVER_URL}images/${pet.image}"
    }

    Scaffold(
        containerColor = BackgroundColor,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundColor),
                title = { Text(if (isEditMode) "Chỉnh sửa thông tin" else "Quản lý chi tiết", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = PrimaryColor)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Tính năng mở rộng */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = null, tint = OnSurfaceVariant)
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 100.dp), // Tránh đè nút cuối màn hình
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. KHU VỰC HIỂN THỊ HÌNH ẢNH Resident Hero
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(4f / 3f)
                        .clip(RoundedCornerShape(32.dp))
                        .background(SurfaceContainerLow)
                ) {
                    SubcomposeAsyncImage(
                        model = fullImageUrl,
                        contentDescription = namePet,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        loading = { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = PrimaryColor) } },
                        error = { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Icon(Icons.Default.Pets, null, modifier = Modifier.size(64.dp), tint = Color.LightGray) } }
                    )

                    // Nhãn hiển thị trạng thái động (State Badge)
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .background(
                                when (statePet) {
                                    "available" -> Color(0xFFDFF6DD)
                                    "reserved" -> Color(0xFFFFF3CD)
                                    "adopted" -> Color(0xFFE2E3E5)
                                    else -> Color.LightGray
                                }, RoundedCornerShape(50.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = statePet.uppercase(),
                            color = when (statePet) {
                                "available" -> Color(0xFF006F42)
                                "reserved" -> Color(0xFF856404)
                                "adopted" -> Color(0xFF383D41)
                                else -> Color.DarkGray
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // 2. KHU VỰC TIÊU ĐỀ TÊN VÀ ĐỊA CHỈ
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        if (isEditMode) {
                            OutlinedTextField(
                                value = namePet,
                                onValueChange = { namePet = it },
                                label = { Text("Tên Thú Cưng") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryColor)
                            )
                        } else {
                            Text(text = namePet, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = PrimaryColor, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Trung tâm Cứu hộ Paws & Hearts", fontSize = 14.sp, color = OnSurfaceVariant)
                            }
                        }
                    }
                    if (!isEditMode) {
                        Text(text = "#${pet.id_pet}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                    }
                }

                // 3. THÔNG SỐ NHANH QUICK STATS (Tuổi, Giới tính, Lượt Xem)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Cột Tuổi
                    Column(
                        modifier = Modifier.weight(1f).background(SurfaceContainerLow, RoundedCornerShape(16.dp)).padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (isEditMode) {
                            OutlinedTextField(
                                value = age,
                                onValueChange = { age = it },
                                label = { Text("Tuổi") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            Text(text = "$age", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                            Text(text = "Tháng tuổi", fontSize = 12.sp, color = OnSurfaceVariant)
                        }
                    }

                    // Cột Giới Tính
                    Column(
                        modifier = Modifier.weight(1f).background(Color(0xFF79F3EA).copy(alpha = 0.15f), RoundedCornerShape(16.dp)).padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (isEditMode) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                RadioButton(selected = gender == "male", onClick = { gender = "male" })
                                Text("Đực", fontSize = 11.sp)
                                RadioButton(selected = gender == "female", onClick = { gender = "female" })
                                Text("Cái", fontSize = 11.sp)
                            }
                        } else {
                            Icon(
                                imageVector = if (gender == "male") Icons.Default.Male else Icons.Default.Female,
                                contentDescription = null,
                                tint = Color(0xFF006A65)
                            )
                            Text(text = if (gender == "male") "Đực" else "Cái", fontSize = 12.sp, color = OnSurfaceVariant)
                        }
                    }

                    // Cột Lượt Xem (Cột mới ánh xạ từ trường `click` trong database)
                    Column(
                        modifier = Modifier.weight(1f).background(SurfaceContainerLow, RoundedCornerShape(16.dp)).padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "${pet.click}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF006A65))
                        Text(text = "Lượt xem", fontSize = 12.sp, color = OnSurfaceVariant)
                    }
                }

                // 4. THÔNG TIN CƠ BẢN & PHÂN LOẠI LOÀI (SPECIES)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = PrimaryColor)
                            Text("Thông tin phân loại", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }

                        if (isEditMode) {
                            Text("Loài (Database Enum):", fontWeight = FontWeight.SemiBold, fontSize = 14.dp.value.sp)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("dog", "cat", "other").forEach { item ->
                                    FilterChip(
                                        selected = species == item,
                                        onClick = { species = item },
                                        label = { Text(item.uppercase()) }
                                    )
                                }
                            }
                        } else {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Phân loại loài", color = OnSurfaceVariant)
                                Text(species.uppercase(), fontWeight = FontWeight.Bold, color = OnSurface)
                            }
                        }
                    }
                }

                // 5. MÔ TẢ CHI TIẾT (DESCRIPTION)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Description, contentDescription = null, tint = PrimaryColor)
                            Text("Mô tả tiểu sử", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }

                        if (isEditMode) {
                            OutlinedTextField(
                                value = description,
                                onValueChange = { description = it },
                                modifier = Modifier.fillMaxWidth().height(120.dp),
                                maxLines = 5
                            )
                        } else {
                            Text(text = description, fontSize = 15.sp, color = OnSurfaceVariant, lineHeight = 22.sp)
                        }
                    }
                }

                // 6. THAY ĐỔI TRẠNG THÁI NHANH (KHI Ở CHẾ ĐỘ SỬA)
                if (isEditMode) {
                    Text("Cập nhật trạng thái cư trú:", fontWeight = FontWeight.Bold, color = OnSurface)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("available", "reserved", "adopted").forEach { stateName ->
                            Button(
                                onClick = { statePet = stateName },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (statePet == stateName) PrimaryColor else SurfaceContainerHigh
                                )
                            ) {
                                Text(stateName.uppercase(), fontSize = 10.sp, color = if (statePet == stateName) Color.White else OnSurface)
                            }
                        }
                    }
                }
            }

            // GIAO DIỆN HÀNG PHÍM CHỨC NĂNG CỐ ĐỊNH Ở ĐÁY (ACTION BUTTONS GROUP)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(BackgroundColor)
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (!isEditMode) {
                        // Nút 1: Chuyển sang chế độ Chỉnh sửa
                        Button(
                            onClick = { isEditMode = true },
                            modifier = Modifier.weight(2f).height(54.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainer),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = OnPrimaryContainer)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sửa thông tin", color = OnPrimaryContainer, fontWeight = FontWeight.Bold)
                        }

                        // Nút 2: Xóa thú cưng khỏi hệ thống
                        Button(
                            onClick = {
                                viewModel.deletePet(pet.id_pet.toString())
                                Toast.makeText(context, "Đã xóa thú cưng!", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            },
                            modifier = Modifier.weight(1f).height(54.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White)
                        }
                    } else {
                        // Khi đang ở chế độ chỉnh sửa -> Hiện nút Hủy và Lưu
                        Button(
                            onClick = { isEditMode = false },
                            modifier = Modifier.weight(1f).height(54.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainerHigh),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Hủy", color = OnSurface)
                        }

                        Button(
                            onClick = {
                                // Gọi API Cập nhật của ViewModel sử dụng dữ liệu mới đã nhập
                                viewModel.updatePetState(
                                    pet = pet.copy(
                                        name_pet = namePet,
                                        species = species,
                                        gender = gender,
                                        age = age.toIntOrNull() ?: pet.age,
                                        description = description,
                                        state = statePet
                                    ),
                                    newState = statePet
                                )
                                Toast.makeText(context, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
                                isEditMode = false
                            },
                            modifier = Modifier.weight(2f).height(54.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Lưu lại", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}