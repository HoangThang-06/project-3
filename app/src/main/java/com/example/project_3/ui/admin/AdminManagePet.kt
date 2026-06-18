package com.example.project_3.ui.admin

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.SubcomposeAsyncImage
import com.example.project_3.data.model.Pet
import com.example.project_3.viewmodel.AdminManagePetViewModel


const val BASE_SERVER_URL = "http://10.0.2.2/project-3/"
val BackgroundColor = Color(0xFFFDF8F5)
val PrimaryColor = Color(0xFFE28754)
val PrimaryFixed = Color(0xFFFFDBC9)
val PrimaryContainer = Color(0xFFFFDBC9)
val OnPrimaryContainer = Color(0xFF331200)
val SurfaceContainerLowest = Color(0xFFFFFFFF)
val SurfaceContainerHigh = Color(0xFFEAE8E7)
val SurfaceContainerLow = Color(0xFFF5F3F3)
val OnSurface = Color(0xFF1B1C1C)
val OnSurfaceVariant = Color(0xFF564338)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminManagePet(
    navController: NavController,
    viewModel: AdminManagePetViewModel = viewModel()
) {
    val petList by viewModel.filteredPets.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedStatusFilter by remember { mutableStateOf("All") }

    Scaffold(
        containerColor = BackgroundColor,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundColor),
                title = { Text("Paws & Hearts", color = PrimaryColor, fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { }) { Icon(Icons.Default.Notifications, contentDescription = null, tint = OnSurfaceVariant) }
                    Box(modifier = Modifier.size(32.dp).background(PrimaryFixed, CircleShape).clip(CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryColor)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                }
            )
        },
        bottomBar = {
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
            NavigationBar(containerColor = SurfaceContainerLowest, tonalElevation = 8.dp) {
                val items = listOf(
                    Triple("Dash", "admin_home", Icons.Default.Home),
                    Triple("Pets", "admin_manage_pet", Icons.Default.Pets),
                    Triple("Apps", "admin_adopt", Icons.Default.Menu),
                    Triple("Social", "admin_social", Icons.Default.Share)
                )
                items.forEach { (title, route, icon) ->
                    NavigationBarItem(
                        selected = currentRoute == route,
                        onClick = {
                            if (currentRoute != route) {
                                navController.navigate(route) {
                                    launchSingleTop = true
                                    restoreState = true
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                }
                            }
                        },
                        icon = { Icon(icon, contentDescription = title) },
                        label = { Text(title, fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = OnPrimaryContainer,
                            selectedTextColor = Color(0xFF1B1C1C),
                            indicatorColor = PrimaryContainer.copy(alpha = 0.4f),
                            unselectedIconColor = OnSurfaceVariant,
                            unselectedTextColor = OnSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryColor)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Quản lý Thú cưng", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                            Text("Monitor residents and status.", fontSize = 14.sp, color = OnSurfaceVariant)
                        }
                        Button(
                            onClick = { navController.navigate("admin_add_pet") },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Pet", tint = Color.White)
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            viewModel.updateSearchQuery(it)
                        },
                        placeholder = { Text("Search pets...", fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SurfaceContainerLow,
                            unfocusedContainerColor = SurfaceContainerLow,
                            focusedBorderColor = PrimaryColor,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                }

                item {
                    val statusChips = listOf("All", "Available", "Reserved", "Adopted")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(statusChips) { status ->
                            val isSelected = selectedStatusFilter == status
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedStatusFilter = status
                                    viewModel.updateStatusFilter(status)
                                },
                                label = { Text(status) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PrimaryColor,
                                    selectedLabelColor = Color.White,
                                    containerColor = SurfaceContainerHigh
                                ),
                                border = null,
                                shape = RoundedCornerShape(50.dp)
                            )
                        }
                    }
                }

                if (petList.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp), contentAlignment = Alignment.Center) {
                            Text("Không tìm thấy thú cưng nào.", color = OnSurfaceVariant, fontSize = 16.sp)
                        }
                    }
                } else {
                    // ĐÃ CẬP NHẬT: Nhấn vào item hoặc nút quản lý đều kích hoạt lưu dữ liệu và chuyển vùng điều hướng
                    items(petList) { pet ->
                        PetRowCard(
                            pet = pet,
                            onPetClick = {
                                viewModel.selectPet(pet)
                                navController.navigate("admin_pet_detail")
                            },
                            onManageClick = {
                                viewModel.selectPet(pet)
                                navController.navigate("admin_pet_detail")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PetRowCard(
    pet: Pet,
    onPetClick: () -> Unit,
    onManageClick: () -> Unit
) {
    val fullImageUrl = if (pet.image.startsWith("images/")) {
        "${BASE_SERVER_URL}${pet.image}"
    } else {
        "${BASE_SERVER_URL}images/${pet.image}"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceContainerLowest, RoundedCornerShape(16.dp))
            .clickable { onPetClick() } // GIẢI PHÁP: Giúp toàn bộ vùng hàng ngang này nhận diện sự kiện click
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .background(SurfaceContainerLow, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
        ) {
            SubcomposeAsyncImage(
                model = fullImageUrl,
                contentDescription = "Ảnh của ${pet.name_pet}",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                loading = {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PrimaryColor, strokeWidth = 2.dp, modifier = Modifier.size(24.dp))
                    }
                },
                error = {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Pets, contentDescription = null, modifier = Modifier.size(48.dp), tint = OnSurfaceVariant.copy(alpha = 0.3f))
                    }
                }
            )

            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(4.dp)
                    .background(
                        when (pet.state.lowercase()) {
                            "available" -> Color(0xFFDFF6DD)
                            "reserved" -> Color(0xFFFFF3CD)
                            "adopted" -> Color(0xFFE2E3E5)
                            else -> Color.LightGray
                        }, RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(pet.state.uppercase(), fontSize = 8.sp, fontWeight = FontWeight.Bold)
            }
        }

        Column(modifier = Modifier.weight(1f).height(96.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(pet.name_pet, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    Text("#${pet.id_pet}", fontSize = 12.sp, color = Color.Gray)
                }
                Text("${pet.species.uppercase()} • ${pet.age} tuổi", fontSize = 14.sp, color = OnSurfaceVariant)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(pet.gender.uppercase(), fontSize = 12.sp, color = OnSurfaceVariant)
                Button(
                    onClick = onManageClick,
                    modifier = Modifier.height(32.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Text("Quản lý", fontSize = 12.sp, color = Color.White)
                }
            }
        }
    }
}