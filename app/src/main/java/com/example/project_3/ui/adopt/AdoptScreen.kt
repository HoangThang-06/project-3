package com.example.project_3.ui.adopt

import androidx.compose.foundation.background
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
import coil.compose.AsyncImage // Import thư viện Coil
import com.example.project_3.data.model.PetItem
import com.example.project_3.viewmodel.AdoptViewModel

@Composable
fun AdoptScreen(adoptViewModel: AdoptViewModel = viewModel()) {
    val petList = adoptViewModel.petList
    val isLoading = adoptViewModel.isLoading

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFBFBFB))
            .padding(horizontal = 16.dp)
    ) {
        // --- Phần Header & Search (Giữ nguyên giao diện mẫu) ---
        Spacer(modifier = Modifier.height(16.dp))
        Text("Tìm kiếm bạn đồng hành", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("Có hàng ngàn thú cưng đang chờ bạn đón về nhà.", color = Color.Gray, fontSize = 14.sp)

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = "", onValueChange = {},
            placeholder = { Text("Tìm kiếm giống loài...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(25.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- Hiển thị danh sách từ API ---
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF8D4000))
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(petList) { pet ->
                    PetCardDynamic(pet)
                }
            }
        }
    }
}

@Composable
fun PetCardDynamic(pet: PetItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            // Load ảnh động từ Server URL
            AsyncImage(
                model = pet.image_url,
                contentDescription = pet.name,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(pet.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Icon(Icons.Default.FavoriteBorder, contentDescription = null, tint = Color.Gray)
                }

                Text(pet.desc, color = Color.Gray, fontSize = 13.sp) // breed_age

                // Tag trạng thái (ví dụ: Cần người nuôi)
                Surface(
                    color = Color(0xFFE0F7F4),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Text(
                        text = pet.tag,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        color = Color(0xFF00BFA5),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, size = 14.dp, contentDescription = null, tint = Color.Gray)
                    Text(pet.location, color = Color.Gray, fontSize = 12.sp)
                }
            }
        }
    }
}