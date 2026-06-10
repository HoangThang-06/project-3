package com.example.project_3.ui.adopt

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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
import com.example.project_3.viewmodel.PetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen(
    navController: NavController,
    petViewModel: PetViewModel = viewModel()
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val currentUserId = sessionManager.getUserId()

    // Tự động tải danh sách thú cưng yêu thích khi mở màn hình
    LaunchedEffect(currentUserId) {
        if (currentUserId != -1) {
            petViewModel.fetchFavoritePets(currentUserId)
        }
    }

    val petList = petViewModel.petList
    val isLoading = petViewModel.isLoading.value
    val errorMessage = petViewModel.errorMessage.value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thú cưng đang theo dõi", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFBFBFB))
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFFFD8C45)
                )
            } else if (petList.isEmpty()) {
                // Hiển thị thông báo nếu danh sách trống
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Danh sách trống",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    Text(
                        text = "Những thú cưng bạn nhấn tim sẽ xuất hiện ở đây.",
                        fontSize = 14.sp,
                        color = Color.LightGray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(petList) { pet ->
                        PetCardDynamic(
                            pet = pet,
                            onClickCard = {
                                navController.navigate("pet_detail/${pet.id_pet}")
                            },
                            onFollowClick = {
                                // Xử lý hủy theo dõi trực tiếp tại trang này
                                petViewModel.followPet(currentUserId, pet.id_pet)

                                // Vì đây là trang danh sách yêu thích, khi bỏ chọn tim
                                // chúng ta có thể xóa luôn chú pet đó khỏi danh sách hiển thị tạm thời
                                petViewModel.petList.remove(pet)

                                Toast.makeText(
                                    context,
                                    "Đã bỏ theo dõi ${pet.name_pet}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }
                }
            }

            // Hiển thị lỗi nếu có
            if (errorMessage.isNotEmpty() && petList.isEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}