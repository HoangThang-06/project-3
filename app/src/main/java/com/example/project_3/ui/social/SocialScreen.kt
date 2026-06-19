package com.example.project_3.ui.social

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.project_3.R
import com.example.project_3.data.local.SessionManager
import com.example.project_3.data.model.Article
import com.example.project_3.data.model.Comment
import com.example.project_3.viewmodel.SocialViewModel

// 🔥 HÀM HELPER: Xử lý tối ưu hóa URL đường dẫn hình ảnh thông minh từ Server PHP XAMPP
fun getFullImageUrl(imagePath: String?): Any {
    if (imagePath.isNullOrEmpty() || imagePath == "default" || imagePath == "null" || imagePath.trim() == "/uploads/articles/") {
        return R.drawable.ic_default_avatar // Trả về ID drawable mặc định nếu dữ liệu rỗng
    }

    val baseUrl = "http://10.0.2.2/project-3"

    return when {
        imagePath.startsWith("/uploads") -> "$baseUrl$imagePath"
        imagePath.startsWith("uploads") -> "$baseUrl/$imagePath"
        imagePath.startsWith("images") -> "$baseUrl/$imagePath"
        imagePath.startsWith("/images") -> "$baseUrl$imagePath"
        else -> if (imagePath.startsWith("/")) "$baseUrl$imagePath" else "$baseUrl/$imagePath"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialScreen(socialViewModel: SocialViewModel = viewModel()) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val currentUserId = sessionManager.getUserId()

    var currentScreen by remember { mutableStateOf("feed") }
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedArticleId by remember { mutableStateOf(-1) }

    LaunchedEffect(currentUserId) {
        socialViewModel.loadArticles(currentUserId)
    }

    val articleList = socialViewModel.articleList
    val isLoading = socialViewModel.isLoading.value

    if (currentScreen == "create") {
        CreateArticleScreen(
            socialViewModel = socialViewModel,
            currentUserId = currentUserId,
            onBack = { currentScreen = "feed" }
        )
    } else {
        Scaffold(
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = {
                        if (currentUserId == -1) {
                            Toast.makeText(context, "Vui lòng đăng nhập để đăng bài!", Toast.LENGTH_SHORT).show()
                        } else {
                            currentScreen = "create"
                        }
                    },
                    modifier = Modifier.height(48.dp).widthIn(min = 140.dp),
                    containerColor = Color(0xFFFD8C45),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(30.dp)
                ) {
                    Icon(painterResource(id = R.drawable.ic_image), null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Đăng bài viết", fontSize = 14.sp)
                }
            },
            containerColor = Color(0xFFFBFBFB)
        ) { paddingValues ->
            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFFFD8C45))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
                ) {
                    items(articleList) { article ->
                        ArticleCard(
                            article = article,
                            onLikeClick = {
                                if (currentUserId == -1) {
                                    Toast.makeText(context, "Vui lòng đăng nhập!", Toast.LENGTH_SHORT).show()
                                } else {
                                    socialViewModel.toggleLikeArticle(currentUserId, article.id_article)
                                }
                            },
                            onCommentClick = {
                                selectedArticleId = article.id_article
                                socialViewModel.openCommentsForArticle(article.id_article)
                                showBottomSheet = true
                            }
                        )
                    }
                }
            }

            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showBottomSheet = false },
                    sheetState = sheetState,
                    containerColor = Color.White,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                ) {
                    CommentSheetContent(
                        socialViewModel = socialViewModel,
                        currentUserId = currentUserId,
                        articleId = selectedArticleId
                    )
                }
            }
        }
    }
}

@Composable
fun ArticleCard(
    article: Article,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header User
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = getFullImageUrl(article.author_avatar),
                    contentDescription = "Author Avatar",
                    modifier = Modifier.size(45.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.ic_default_avatar),
                    error = painterResource(id = R.drawable.ic_default_avatar)
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = article.author_name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    // 💡 SỬA: Đồng bộ gọi đúng tên trường biến snake_case từ JSON API trả về
                    Text(text = "${article.create_at} • ${article.authorAddress}", color = Color.Gray, fontSize = 12.sp)
                }
                IconButton(onClick = { }) { Icon(Icons.Default.MoreVert, null, tint = Color.Gray) }
            }

            Spacer(Modifier.height(12.dp))

            // Ảnh bài đăng (Tự động chuẩn hóa link thông qua helper)
            AsyncImage(
                model = getFullImageUrl(article.image),
                contentDescription = "Post Image",
                modifier = Modifier.fillMaxWidth().height(280.dp).clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Crop,
                error = painterResource(id = R.drawable.logo_paws_hearts) // Hiện ảnh logo thay thế nếu đường dẫn die
            )

            // Thanh tương tác (Like, Comment, Share)
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onLikeClick() }, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = if (article.isLiked == 1) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = if (article.isLiked == 1) Color.Red else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(" ${article.likes_count}", Modifier.padding(start = 4.dp, end = 16.dp))

                Row(
                    modifier = Modifier.clickable { onCommentClick() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(painterResource(id = R.drawable.ic_comment), null, tint = Color.Gray, modifier = Modifier.size(24.dp))
                    Text(" ${article.comments_count}", Modifier.padding(start = 4.dp, end = 16.dp))
                }

                Icon(Icons.Default.Share, null, tint = Color.Gray, modifier = Modifier.size(24.dp))
            }

            // Nội dung Text bài đăng
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("${article.author_name} ") }
                    append(article.content)
                },
                fontSize = 14.sp, lineHeight = 20.sp
            )

            Spacer(Modifier.height(12.dp))

            // Hashtags
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                val tags = article.category.split("#")
                tags.forEach { tag ->
                    val cleanTag = tag.trim()
                    if (cleanTag.isNotEmpty()) {
                        val isHappy = cleanTag.contains("hanhphuc") || cleanTag.contains("yeuthuong") || cleanTag.contains("meomayman")
                        Surface(
                            color = if (isHappy) Color(0xFFE0F7FA) else Color(0xFFFCE4EC),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "#$cleanTag",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                color = if (isHappy) Color(0xFF00ACC1) else Color(0xFFD81B60),
                                fontSize = 12.sp, fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentSheetContent(
    socialViewModel: SocialViewModel,
    currentUserId: Int,
    articleId: Int
) {
    val context = LocalContext.current
    val comments = socialViewModel.commentList
    val isCommentsLoading = socialViewModel.isCommentsLoading.value
    val hasMore = socialViewModel.hasMoreComments.value

    var typedCommentText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.85f)
            .padding(bottom = WindowInsets.ime.asPaddingValues().calculateBottomPadding())
    ) {
        Text(
            text = "Bình luận",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(vertical = 12.dp).align(Alignment.CenterHorizontally)
        )
        HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)

        Box(modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 16.dp)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(comments) { comment ->
                    CommentItem(comment)
                }

                if (hasMore) {
                    item {
                        Box(Modifier.fillMaxWidth().padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                            if (isCommentsLoading) {
                                CircularProgressIndicator(color = Color(0xFFFD8C45), modifier = Modifier.size(24.dp))
                            } else {
                                Text(
                                    text = "Xem thêm bình luận...",
                                    color = Color(0xFFFD8C45),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    modifier = Modifier
                                        .clickable { socialViewModel.loadMoreComments() }
                                        .padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }

            if (isCommentsLoading && comments.isEmpty()) {
                CircularProgressIndicator(color = Color(0xFFFD8C45), modifier = Modifier.align(Alignment.Center))
            }
        }

        HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = typedCommentText,
                onValueChange = { typedCommentText = it },
                placeholder = { Text("Viết bình luận...", fontSize = 14.sp, color = Color.Gray) },
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF0F2F5),
                    unfocusedContainerColor = Color(0xFFF0F2F5),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(24.dp),
                maxLines = 3
            )

            Spacer(Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (currentUserId == -1) {
                        Toast.makeText(context, "Vui lòng đăng nhập để bình luận!", Toast.LENGTH_SHORT).show()
                    } else {
                        socialViewModel.sendComment(currentUserId, articleId, typedCommentText) {
                            typedCommentText = ""
                        }
                    }
                },
                enabled = typedCommentText.trim().isNotEmpty()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_send),
                    contentDescription = "Send",
                    tint = if (typedCommentText.trim().isNotEmpty()) Color(0xFFFD8C45) else Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun CommentItem(comment: Comment) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        AsyncImage(
            model = getFullImageUrl(comment.user_avatar),
            contentDescription = "Comment User Avatar",
            modifier = Modifier.size(36.dp).clip(CircleShape),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.ic_default_avatar),
            error = painterResource(id = R.drawable.ic_default_avatar)
        )
        Spacer(Modifier.width(8.dp))
        Column {
            Box(
                modifier = Modifier
                    .background(Color(0xFFF0F2F5), shape = RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Column {
                    Text(text = comment.user_name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(Modifier.height(2.dp))
                    Text(text = comment.content, fontSize = 14.sp, color = Color.Black)
                }
            }
            Text(
                text = comment.create_at,
                fontSize = 11.sp,
                color = Color.Gray,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }
    }
}