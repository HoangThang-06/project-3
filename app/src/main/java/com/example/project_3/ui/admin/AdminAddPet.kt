package com.example.project_3.ui.admin

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
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
import coil.compose.rememberAsyncImagePainter
import com.example.project_3.viewmodel.AdminAddPetViewModel

// Giả định các biến màu sắc hệ thống của dự án bạn
val OutlineVariant = Color(0xFFDDC1B3)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAddPet(
    navController: NavController,
    viewModel: AdminAddPetViewModel = viewModel()
) {
    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsState()
    val isSuccess by viewModel.isSuccess.collectAsState()
    val selectedTraits by viewModel.selectedPersonalities.collectAsState()

    var petName by remember { mutableStateOf("") }
    var breed by remember { mutableStateOf("") }
    var healthStatus by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Đồng bộ chuẩn giá trị khởi tạo theo Enum của Database
    var selectedSpecies by remember { mutableStateOf("dog") } // dog, cat, other
    var selectedAgeLabel by remember { mutableStateOf("Trẻ") }
    var selectedGender by remember { mutableStateOf("male") } // male, female

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            Toast.makeText(context, "Thêm thú cưng mới thành công!", Toast.LENGTH_SHORT).show()
            viewModel.resetSuccess()
            navController.popBackStack()
        }
    }

    Scaffold(
        containerColor = BackgroundColor,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundColor),
                title = { Text(text = "Add New Pet", color = PrimaryColor, fontSize = 24.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        // Khắc phục cảnh báo bằng cách sử dụng phiên bản AutoMirrored
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PrimaryColor)
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.AutoMirrored.Filled.Help, contentDescription = "Help", tint = PrimaryColor)
                    }
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
                        icon = { Icon(imageVector = icon, contentDescription = title) },
                        label = { Text(text = title, fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = OnPrimaryContainer,
                            selectedTextColor = Color(0xFF1B1C1C),
                            indicatorColor = PrimaryFixed.copy(alpha = 0.6f),
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
                modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // SECTION 1: HÌNH ẢNH THÚ CƯNG
                item {
                    Text(
                        text = "Hình ảnh thú cưng (Yêu cầu 1 ảnh duy nhất)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OnSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .background(SurfaceContainer, RoundedCornerShape(12.dp))
                                .border(2.dp, OutlineVariant, RoundedCornerShape(12.dp))
                                .clickable { imagePickerLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedImageUri == null) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.AddCircle,
                                        contentDescription = "Thêm ảnh",
                                        tint = PrimaryColor,
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Text(
                                        text = "Chọn ảnh",
                                        fontSize = 12.sp,
                                        color = OnSurface,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            } else {
                                Image(
                                    painter = rememberAsyncImagePainter(selectedImageUri),
                                    contentDescription = "Ảnh thú cưng đã chọn",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }

                // SECTION 2: TÊN THÚ CƯNG & LOÀI (Đồng bộ Enum DB)
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Tên thú cưng", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = OnSurfaceVariant)
                            OutlinedTextField(
                                value = petName,
                                onValueChange = { petName = it },
                                placeholder = { Text("Nhập tên...") },
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryColor,
                                    unfocusedBorderColor = OutlineVariant,
                                    focusedContainerColor = SurfaceContainerLowest,
                                    unfocusedContainerColor = SurfaceContainerLowest
                                )
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text("Loài", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = OnSurfaceVariant)
                            var expanded by remember { mutableStateOf(false) }
                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = { expanded = !expanded },
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                OutlinedTextField(
                                    value = if (selectedSpecies == "dog") "Chó" else if (selectedSpecies == "cat") "Mèo" else "Khác",
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                    // Khắc phục cảnh báo menuAnchor phiên bản mới nhất bằng cách chỉ định rõ MenuAnchorType
                                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true).fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = PrimaryColor,
                                        unfocusedBorderColor = OutlineVariant,
                                        focusedContainerColor = SurfaceContainerLowest,
                                        unfocusedContainerColor = SurfaceContainerLowest
                                    )
                                )
                                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                    DropdownMenuItem(text = { Text("Chó") }, onClick = { selectedSpecies = "dog"; expanded = false })
                                    DropdownMenuItem(text = { Text("Mèo") }, onClick = { selectedSpecies = "cat"; expanded = false })
                                    DropdownMenuItem(text = { Text("Khác") }, onClick = { selectedSpecies = "other"; expanded = false })
                                }
                            }
                        }
                    }
                }

                // SECTION 3: GIỐNG & TUỔI
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Giống", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = OnSurfaceVariant)
                            OutlinedTextField(
                                value = breed,
                                onValueChange = { breed = it },
                                placeholder = { Text("Ví dụ: Golden...") },
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryColor,
                                    unfocusedBorderColor = OutlineVariant,
                                    focusedContainerColor = SurfaceContainerLowest,
                                    unfocusedContainerColor = SurfaceContainerLowest
                                )
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text("Tuổi", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = OnSurfaceVariant)
                            var expandedAge by remember { mutableStateOf(false) }
                            ExposedDropdownMenuBox(
                                expanded = expandedAge,
                                onExpandedChange = { expandedAge = !expandedAge },
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                OutlinedTextField(
                                    value = selectedAgeLabel,
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedAge) },
                                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true).fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = PrimaryColor,
                                        unfocusedBorderColor = OutlineVariant,
                                        focusedContainerColor = SurfaceContainerLowest,
                                        unfocusedContainerColor = SurfaceContainerLowest
                                    )
                                )
                                ExposedDropdownMenu(expanded = expandedAge, onDismissRequest = { expandedAge = false }) {
                                    listOf("Sơ sinh", "Trẻ", "Trưởng thành", "Già").forEach { ageOption ->
                                        DropdownMenuItem(text = { Text(ageOption) }, onClick = { selectedAgeLabel = ageOption; expandedAge = false })
                                    }
                                }
                            }
                        }
                    }
                }

                // SECTION 4: GIỚI TÍNH (male / female)
                item {
                    Text("Giới tính", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = OnSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        val isMale = selectedGender == "male"
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isMale) PrimaryColor else Color.Transparent)
                                .border(1.dp, if (isMale) PrimaryColor else OutlineVariant, RoundedCornerShape(8.dp))
                                .clickable { selectedGender = "male" },
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Male, contentDescription = null, tint = if (isMale) Color.White else PrimaryColor)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Đực", color = if (isMale) Color.White else OnSurface, fontWeight = FontWeight.Medium)
                        }

                        val isFemale = selectedGender == "female"
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isFemale) PrimaryColor else Color.Transparent)
                                .border(1.dp, if (isFemale) PrimaryColor else OutlineVariant, RoundedCornerShape(8.dp))
                                .clickable { selectedGender = "female" },
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Female, contentDescription = null, tint = if (isFemale) Color.White else PrimaryColor)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Cái", color = if (isFemale) Color.White else OnSurface, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                // SECTION 5: TÌNH TRẠNG SỨC KHỎE
                item {
                    Text("Tình trạng sức khỏe", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = OnSurfaceVariant)
                    OutlinedTextField(
                        value = healthStatus,
                        onValueChange = { healthStatus = it },
                        placeholder = { Text("Đã tiêm phòng, tẩy giun...") },
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        minLines = 3,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryColor,
                            unfocusedBorderColor = OutlineVariant,
                            focusedContainerColor = SurfaceContainerLowest,
                            unfocusedContainerColor = SurfaceContainerLowest
                        )
                    )
                }

                // SECTION 6: ĐẶC ĐIỂM TÍNH CÁCH
                item {
                    Text("Đặc điểm tính cách", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = OnSurfaceVariant)
                    Spacer(modifier = Modifier.height(6.dp))
                    val traits = listOf("Thân thiện", "Nhút nhát", "Năng động", "Ham ăn")

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(traits) { trait ->
                            val isSelected = selectedTraits.contains(trait)
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.togglePersonality(trait) },
                                label = { Text(trait, fontSize = 14.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PrimaryFixed,
                                    selectedLabelColor = PrimaryColor,
                                    containerColor = SurfaceContainerLowest,
                                    labelColor = OnSurfaceVariant
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = if (isSelected) PrimaryColor else OutlineVariant,
                                    selectedBorderColor = PrimaryColor
                                ),
                                shape = RoundedCornerShape(50.dp)
                            )
                        }
                    }
                }

                // SECTION 7: MÔ TẢ CHI TIẾT
                item {
                    Text("Mô tả thêm", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = OnSurfaceVariant)
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        placeholder = { Text("Kể thêm về người bạn nhỏ này...") },
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        minLines = 4,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryColor,
                            unfocusedBorderColor = OutlineVariant,
                            focusedContainerColor = SurfaceContainerLowest,
                            unfocusedContainerColor = SurfaceContainerLowest
                        )
                    )
                }

                // SECTION 8: NÚT LƯU THÔNG TIN
                item {
                    Button(
                        onClick = {
                            if (petName.trim().isEmpty()) {
                                Toast.makeText(context, "Vui lòng nhập tên thú cưng", Toast.LENGTH_SHORT).show()
                            } else if (selectedImageUri == null) {
                                Toast.makeText(context, "Vui lòng chọn ảnh cho thú cưng", Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.savePet(
                                    context = context,
                                    name = petName,
                                    gender = selectedGender,
                                    description = description,
                                    ageLabel = selectedAgeLabel,
                                    species = selectedSpecies,
                                    healthStatus = healthStatus,
                                    imageUri = selectedImageUri
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(54.dp).padding(vertical = 4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Lưu thông tin", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}