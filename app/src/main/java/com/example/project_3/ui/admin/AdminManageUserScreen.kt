package com.example.project_3.ui.admin

import android.app.DatePickerDialog
import androidx.compose.foundation.BorderStroke
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.rememberAsyncImagePainter
import com.example.project_3.data.model.User
import com.example.project_3.viewmodel.AdminManageUserViewModel
import java.util.Calendar
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource

// BẢNG MÀU ĐỒNG BỘ 100% VỚI FILE HTML WEB ADMIN PORTAL
private object AdminPortalColors {
    val Primary = Color(0xFF9B4500)
    val PrimaryContainer = Color(0xFFFF8C42)
    val OnPrimaryContainer = Color(0xFF6A2D00)
    val SecondaryContainer = Color(0xFF79F3EA)
    val OnSecondaryContainer = Color(0xFF006F69)
    val Background = Color(0xFFFBF9F8)
    val Surface = Color(0xFFFBF9F8)
    val SurfaceContainerLow = Color(0xFFF5F3F3)
    val SurfaceContainerHigh = Color(0xFFEAE8E7)
    val SurfaceContainerLowest = Color(0xFFFFFFFF)
    val OnSurface = Color(0xFF1B1C1C)
    val OnSurfaceVariant = Color(0xFF564338)
    val OutlineVariant = Color(0xFFDDC1B3)
    val Error = Color(0xFFBA1A1A)
    val ErrorContainer = Color(0xFFFFDAD6)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminManageUserScreen(
    currentUserId: String,
    navController: NavController,
    viewModel: AdminManageUserViewModel
) {
    val context = LocalContext.current
    val sessionManager = remember { com.example.project_3.data.local.SessionManager(context) }
    val savedUserId = remember { sessionManager.getUserId() }
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    // Các trạng thái điều khiển Modal chỉnh sửa thông tin người dùng
    var showEditBottomSheet by remember { mutableStateOf(false) }
    var selectedUserForEdit by remember { mutableStateOf<User?>(null) }

    // Đường dẫn ảnh đại diện mặc định của hệ thống Admin
    val defaultAvatarUrl = "http://10.0.2.2/project-3/images/avarta_mac_dinh.jpg"
    // Lấy ra ID người dùng đang active từ logic cũ để truyền sang tab khác
    val activeAdminId = if (currentUserId.isNotEmpty()) currentUserId else savedUserId.toString()

    // Đồng bộ luồng nạp dữ liệu ban đầu
    LaunchedEffect(currentUserId, savedUserId) {
        if (currentUserId.isNotEmpty()) {
            viewModel.loadAllUsers(currentUserId)
        } else if (savedUserId != -1) {
            viewModel.loadAllUsers(savedUserId.toString())
        } else {
            viewModel.loadAllUsers("1")
        }
    }

    Scaffold(
        containerColor = AdminPortalColors.Background,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = AdminPortalColors.Primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Admin Portal",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = AdminPortalColors.Primary
                        )
                    }
                },
                actions = {
                    // 🛠️ CHỈ SỬA KHỐI NÀY: Thay đổi đường dẫn ảnh mặc định và xử lý sự kiện click chuyển sang tab admin_profile
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AdminPortalColors.SurfaceContainerHigh)
                            .clickable {
                                if (activeAdminId.isNotEmpty() && activeAdminId != "-1") {
                                    // Điều hướng sang màn hình Profile của Admin theo ID tương tự AdminManagePet
                                    navController.navigate("admin_profile/$activeAdminId") {
                                        launchSingleTop = true
                                    }
                                } else {
                                    Toast.makeText(context, "Không xác định được phiên đăng nhập!", Toast.LENGTH_SHORT).show()
                                }
                            }
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(model = defaultAvatarUrl),
                            contentDescription = "Admin Avatar Mặc Định",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AdminPortalColors.Surface)
            )
        },
        bottomBar = {
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

            NavigationBar(
                containerColor = AdminPortalColors.SurfaceContainerLowest,
                tonalElevation = 8.dp
            ) {
                val items = listOf(
                    Triple("Dash", "admin_home", Icons.Default.Home),
                    Triple("Pets", "admin_manage_pet", Icons.Default.Pets),
                    Triple("Apps", "admin_adopt", Icons.Default.Menu),
                    Triple("Social", "admin_social", Icons.Default.Share),
                    Triple("Users", "admin_manage_user", Icons.Default.People)
                )

                items.forEach { (title, route, icon) ->
                    NavigationBarItem(
                        selected = currentRoute?.startsWith(route) == true,
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
                            selectedIconColor = AdminPortalColors.OnPrimaryContainer,
                            selectedTextColor = AdminPortalColors.OnSurface,
                            indicatorColor = AdminPortalColors.PrimaryContainer.copy(alpha = 0.4f),
                            unselectedIconColor = AdminPortalColors.OnSurfaceVariant,
                            unselectedTextColor = AdminPortalColors.OnSurfaceVariant
                        )
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    Toast.makeText(context, "Tính năng thêm tài khoản mới", Toast.LENGTH_SHORT).show()
                },
                containerColor = AdminPortalColors.Primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(imageVector = Icons.Default.PersonAdd, contentDescription = "Add User", modifier = Modifier.size(28.dp))
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = AdminPortalColors.Primary
                )
            } else if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage ?: "Lỗi không xác định",
                    color = AdminPortalColors.Error,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp)
                )
            } else {
                val filteredUsers = uiState.users.filter {
                    it.fullname?.contains(searchQuery, ignoreCase = true) == true ||
                            it.email.contains(searchQuery, ignoreCase = true) ||
                            it.username.contains(searchQuery, ignoreCase = true)
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Column(modifier = Modifier.padding(bottom = 8.dp)) {
                            Text(
                                text = "Quản lý người dùng",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold,
                                color = AdminPortalColors.OnSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Theo dõi và quản lý danh sách thành viên trong hệ thống.",
                                fontSize = 14.sp,
                                color = AdminPortalColors.OnSurfaceVariant
                            )
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Tìm kiếm theo tên hoặc email...", color = Color.Gray, fontSize = 14.sp) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AdminPortalColors.OnSurfaceVariant) },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = CircleShape,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = AdminPortalColors.SurfaceContainerLow,
                                unfocusedContainerColor = AdminPortalColors.SurfaceContainerLow,
                                focusedBorderColor = AdminPortalColors.PrimaryContainer,
                                unfocusedBorderColor = Color.Transparent
                            ),
                            singleLine = true
                        )
                    }

                    // Bento Grid thống kê động
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(vertical = 8.dp)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(AdminPortalColors.PrimaryContainer)
                                    .padding(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxHeight(),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Tổng số người dùng", fontSize = 13.sp, color = AdminPortalColors.OnPrimaryContainer.copy(alpha = 0.8f), fontWeight = FontWeight.SemiBold)
                                        Text("${uiState.users.size}", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = AdminPortalColors.OnPrimaryContainer)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.TrendingUp, contentDescription = null, modifier = Modifier.size(14.dp), tint = AdminPortalColors.OnPrimaryContainer.copy(alpha = 0.8f))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("12% so với tháng trước", fontSize = 11.sp, color = AdminPortalColors.OnPrimaryContainer.copy(alpha = 0.8f))
                                    }
                                }
                                Icon(
                                    imageVector = Icons.Default.Group,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(100.dp)
                                        .align(Alignment.BottomEnd)
                                        .offset(x = 10.dp, y = 10.dp),
                                    tint = AdminPortalColors.OnPrimaryContainer.copy(alpha = 0.08f)
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(100.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(AdminPortalColors.SecondaryContainer)
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Mới tuần này", fontSize = 13.sp, color = AdminPortalColors.OnSecondaryContainer.copy(alpha = 0.8f), fontWeight = FontWeight.SemiBold)
                                    Text("+42", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = AdminPortalColors.OnSecondaryContainer)
                                    Text("Đang hoạt động", fontSize = 11.sp, color = AdminPortalColors.OnSecondaryContainer.copy(alpha = 0.6f))
                                }

                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(100.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(AdminPortalColors.SurfaceContainerHigh)
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Yêu cầu hỗ trợ", fontSize = 13.sp, color = AdminPortalColors.OnSurfaceVariant, fontWeight = FontWeight.SemiBold)
                                    Text("15", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = AdminPortalColors.OnSurface)
                                    Icon(
                                        imageVector = Icons.Default.PendingActions,
                                        contentDescription = null,
                                        tint = AdminPortalColors.Primary,
                                        modifier = Modifier.size(20.dp).align(Alignment.End)
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Danh sách chi tiết", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AdminPortalColors.OnSurface)
                            Row(
                                modifier = Modifier.clickable { },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = AdminPortalColors.Primary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Lọc", color = AdminPortalColors.Primary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    items(filteredUsers, key = { it.id_user }) { user ->
                        AdminUserRowItem(
                            user = user,
                            onEditClick = {
                                selectedUserForEdit = user
                                showEditBottomSheet = true
                            },
                            onDeleteClick = {
                                viewModel.deleteUser(user.id_user.toString(), activeAdminId)
                            }
                        )
                    }
                }
            }
        }
    }

    if (showEditBottomSheet && selectedUserForEdit != null) {
        EditUserBottomSheet(
            user = selectedUserForEdit!!,
            onDismiss = {
                showEditBottomSheet = false
                selectedUserForEdit = null
            },
            onConfirmUpdate = { fullname, phone, birthday, gender, address, email ->
                viewModel.updateUserProfile(
                    idUser = selectedUserForEdit!!.id_user,
                    fullname = fullname,
                    phone = phone,
                    birthday = birthday,
                    gender = gender,
                    address = address,
                    email = email,
                    currentUserId = activeAdminId
                )
                showEditBottomSheet = false
                selectedUserForEdit = null
                Toast.makeText(context, "Cập nhật tài khoản thành công!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditUserBottomSheet(
    user: User,
    onDismiss: () -> Unit,
    onConfirmUpdate: (fullname: String, phone: String, birthday: String, gender: String, address: String, email: String) -> Unit
) {
    val context = LocalContext.current

    var fullname by remember { mutableStateOf(user.fullname ?: "") }
    var phone by remember { mutableStateOf(user.phone ?: "") }
    var birthday by remember { mutableStateOf(user.birthday ?: "") }
    var gender by remember { mutableStateOf(user.gender ?: "Nam") }
    var address by remember { mutableStateOf(user.address ?: "") }
    var email by remember { mutableStateOf(user.email) }

    val genderOptions = listOf("Nam", "Nu")

    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val formattedMonth = String.format("%02d", month + 1)
            val formattedDay = String.format("%02d", dayOfMonth)
            birthday = "$year-$formattedMonth-$formattedDay"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val scrollState = rememberScrollState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = AdminPortalColors.SurfaceContainerLowest,
        dragHandle = { BottomSheetDefaults.DragHandle(color = AdminPortalColors.OutlineVariant) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sửa thông tin tài khoản",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = AdminPortalColors.OnSurface
                )
                Text(
                    text = "ID: ${user.id_user} | @${user.username}",
                    fontSize = 13.sp,
                    color = AdminPortalColors.OnSurfaceVariant
                )
            }

            HorizontalDivider(color = AdminPortalColors.SurfaceContainerHigh)

            OutlinedTextField(
                value = fullname,
                onValueChange = { fullname = it },
                label = { Text("Họ và tên") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AdminPortalColors.Primary)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email liên lạc") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AdminPortalColors.Primary)
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Số điện thoại") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AdminPortalColors.Primary)
            )

            OutlinedTextField(
                value = birthday,
                onValueChange = {},
                label = { Text("Ngày sinh (YYYY-MM-DD)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        datePickerDialog.show()
                    },
                enabled = false,
                readOnly = true,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Chọn ngày sinh",
                        tint = AdminPortalColors.Primary
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = AdminPortalColors.OnSurface,
                    disabledLabelColor = AdminPortalColors.OnSurfaceVariant,
                    disabledBorderColor = Color.Gray,
                    disabledTrailingIconColor = AdminPortalColors.Primary
                )
            )

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "Giới tính", fontSize = 14.sp, color = AdminPortalColors.OnSurfaceVariant, fontWeight = FontWeight.Medium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    genderOptions.forEach { option ->
                        val isSelected = (option == gender)
                        Row(
                            modifier = Modifier
                                .selectable(
                                    selected = isSelected,
                                    onClick = { gender = option },
                                    role = Role.RadioButton
                                )
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { gender = option },
                                colors = RadioButtonDefaults.colors(selectedColor = AdminPortalColors.Primary)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (option == "Nam") "Nam" else "Nữ",
                                fontSize = 15.sp,
                                color = AdminPortalColors.OnSurface
                            )
                        }
                    }
                }
            }

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Địa chỉ cư trú") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AdminPortalColors.Primary)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, AdminPortalColors.Primary)
                ) {
                    Text("Hủy bỏ", color = AdminPortalColors.Primary)
                }

                Button(
                    onClick = {
                        if (email.trim().isEmpty() || fullname.trim().isEmpty()) {
                            Toast.makeText(context, "Họ tên và Email không được để trống!", Toast.LENGTH_SHORT).show()
                        } else {
                            onConfirmUpdate(fullname, phone, birthday, gender, address, email)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AdminPortalColors.Primary)
                ) {
                    Text("Lưu thay đổi", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun AdminUserRowItem(
    user: User,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val isUserAdmin = user.role.equals("admin", ignoreCase = true)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AdminPortalColors.SurfaceContainerLowest),
        border = BorderStroke(1.dp, AdminPortalColors.OutlineVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .border(1.dp, AdminPortalColors.PrimaryContainer.copy(alpha = 0.5f), CircleShape)
            ) {
                if (!user.avatar.isNullOrEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(user.avatar),
                        contentDescription = "Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Default Avatar",
                        modifier = Modifier.fillMaxSize(),
                        tint = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = user.fullname ?: user.username,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = AdminPortalColors.OnSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(6.dp))

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(
                                if (isUserAdmin) AdminPortalColors.SecondaryContainer
                                else AdminPortalColors.SurfaceContainerHigh
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isUserAdmin) "ADMIN" else "USER",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isUserAdmin) AdminPortalColors.OnSecondaryContainer else AdminPortalColors.OnSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = user.email,
                    fontSize = 13.sp,
                    color = AdminPortalColors.OnSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier
                        .size(36.dp)
                        .background(AdminPortalColors.SurfaceContainerLow, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Sửa",
                        tint = AdminPortalColors.Primary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier
                        .size(36.dp)
                        .background(AdminPortalColors.ErrorContainer.copy(alpha = 0.8f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Xóa",
                        tint = AdminPortalColors.Error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}