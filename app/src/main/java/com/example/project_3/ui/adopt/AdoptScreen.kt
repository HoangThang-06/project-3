package com.example.project_3.ui.adopt

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.project_3.data.local.SessionManager
import com.example.project_3.data.model.Pet
import com.example.project_3.viewmodel.PetViewModel

@Composable
fun AdoptScreen(
    navController: NavController,
    petViewModel: PetViewModel = viewModel()
) {
    val context = LocalContext.current

    val sessionManager = remember { SessionManager(context) }
    val currentUserId = sessionManager.getUserId()

    LaunchedEffect(currentUserId) {
        petViewModel.fetchPets(currentUserId)
    }

    val petList = petViewModel.petList
    val isLoading = petViewModel.isLoading.value
    val errorMessage = petViewModel.errorMessage.value

    if (errorMessage.isNotEmpty()) {
        Text(errorMessage, color = Color.Red)
    }

    var search by remember { mutableStateOf("") }

    val filteredPets = remember(search, petList) {
        petList.filter {
            it.name_pet.contains(search, ignoreCase = true) ||
                    it.species.contains(search, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFBFBFB))
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text("Tìm kiếm bạn đồng hành", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("Có hàng ngàn thú cưng đang chờ bạn đón về nhà.", color = Color.Gray, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            placeholder = { Text("Tìm kiếm giống loài...") },
            leadingIcon = { Icon(Icons.Default.Search, null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(25.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFFD8C45))
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(filteredPets) { pet ->
                    PetCardDynamic(
                        pet = pet,
                        onClickCard = {
                            // CLICK VÀO THẺ: Chỉ thực hiện chuyển màn hình chi tiết thú cưng
                            navController.navigate("pet_detail/${pet.id_pet}")
                        },
                        onFollowClick = {
                            // CLICK VÀO TRÁI TIM: Xử lý bật/tắt yêu thích
                            if (currentUserId == -1) {
                                Toast.makeText(
                                    context,
                                    "Vui lòng đăng nhập để thực hiện tính năng này!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                petViewModel.followPet(currentUserId, pet.id_pet)

                                // Đọc trạng thái hiện tại TRƯỚC KHI ĐỔI để đưa ra thông báo động chính xác 100%
                                if (pet.isFollowed == 1) {
                                    Toast.makeText(context, "Đã xóa ${pet.name_pet} khỏi danh sách theo dõi", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Đã thêm ${pet.name_pet} vào danh sách theo dõi", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PetCardDynamic(
    pet: Pet,
    onClickCard: () -> Unit,
    onFollowClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClickCard() }, // Đã sửa: Click vào card chỉ xem chi tiết
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            AsyncImage(
                model = "http://10.0.2.2/project-3/upload${pet.image}",
                contentDescription = pet.name_pet,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(pet.name_pet, fontWeight = FontWeight.Bold, fontSize = 18.sp)

                    // BIẾN ICON TIM THÀNH NÚT BẤM ĐỘC LẬP (IconButton)
                    IconButton(
                        onClick = { onFollowClick() },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (pet.isFollowed == 1) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            tint = if (pet.isFollowed == 1) Color.Red else Color.Gray
                        )
                    }
                }

                val loaiPet = if(pet.species == "dog") "Chó" else if(pet.species == "cat") "Mèo" else pet.species
                Text("$loaiPet • ${pet.age} tuổi", color = Color.Gray, fontSize = 13.sp)

                Surface(
                    color = Color(0xFFE0F7F4),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Text(
                        text = if(pet.state == "available") "Sẵn sàng nhận nuôi" else pet.state,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        color = Color(0xFF00BFA5),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        if(pet.gender == "male") "Giống đực" else "Giống cái",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}