package com.example.project_3.ui.adopt

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable // THÊM IMPORT NÀY
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController // THÊM IMPORT NÀY
import coil.compose.AsyncImage

import com.example.project_3.data.model.Pet
import com.example.project_3.viewmodel.PetViewModel

@Composable
fun AdoptScreen(
    navController: NavController, // 1. THÊM THAM SỐ ĐIỀU HƯỚNG VÀO ĐÂY
    petViewModel: PetViewModel = viewModel()
) {
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
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(filteredPets) { pet ->
                    // 2. TRUYỀN THÊM onClick ĐỂ ĐIỀU HƯỚNG THEO ID_PET
                    PetCardDynamic(
                        pet = pet,
                        onClick = {
                            navController.navigate("pet_detail/${pet.id_pet}")
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
    onClick: () -> Unit // 3. THÊM THAM SỐ ONCLICK CHO CARD
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }, // 4. KHI BẤM VÀO CARD SẼ GỌI HÀM ĐIỀU HƯỚNG
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            // Lưu ý: Đảm bảo đường dẫn này nối với chuỗi `/images/...` trong DB ra đúng URL ảnh trên XAMPP
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
                    Icon(Icons.Default.FavoriteBorder, contentDescription = null, tint = Color.Gray)
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