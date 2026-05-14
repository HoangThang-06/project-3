package com.example.project_3.ui.social

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import com.example.project_3.data.model.Article
import com.example.project_3.viewmodel.SocialViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialScreen(socialViewModel: SocialViewModel = viewModel()) {
    val articleList = socialViewModel.articleList
    val isLoading = socialViewModel.isLoading.value

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { /* Mở màn hình đăng bài */ },
                // Thêm modifier để giới hạn chiều cao và ép nút nhỏ lại
                modifier = Modifier
                    .height(48.dp)
                    .widthIn(min = 120.dp),
                containerColor = Color(0xFFFD8C45),
                contentColor = Color.White,
                shape = RoundedCornerShape(30.dp)
            ) {
                // Thu nhỏ icon lại một chút
                Icon(
                    painterResource(id = R.drawable.ic_image),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Đăng Story", fontSize = 14.sp)
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
            ) {
                items(articleList) { article ->
                    ArticleCard(article)
                }
            }
        }
    }
}

@Composable
fun ArticleCard(article: Article) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Thông tin User
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = "http://10.0.2.2/project-3/upload${article.author_avatar}",
                    contentDescription = null,
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = article.author_name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "${article.create_at} • Hà Nội",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
                IconButton(onClick = { }) {
                    Icon(Icons.Default.MoreVert, contentDescription = null, tint = Color.Gray)
                }
            }

            Spacer(Modifier.height(12.dp))

            // Nội dung ảnh bài đăng
            AsyncImage(
                model = "http://10.0.2.2/project-3/upload${article.image}",
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Crop
            )

            // Thanh tương tác (Like, Comment, Share, Save)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.FavoriteBorder, null, tint = Color.Gray, modifier = Modifier.size(24.dp))
                Text(" ${article.likes_count}", Modifier.padding(start = 4.dp, end = 16.dp))

                Icon(
                    painter = painterResource(id = R.drawable.ic_comment),
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
                Text(" ${article.comments_count}", Modifier.padding(start = 4.dp, end = 16.dp))

                Icon(Icons.Default.Share, null, tint = Color.Gray, modifier = Modifier.size(24.dp))

                Spacer(Modifier.weight(1f))

                Icon(
                    painter = painterResource(id = R.drawable.ic_bookmark),
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Text Nội dung bài đăng
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("${article.author_name} ")
                    }
                    append(article.content)
                },
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            Spacer(Modifier.height(8.dp))

            // Danh sách Hashtag (Category)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val tags = article.category.split(" ")
                tags.forEach { tag ->
                    if (tag.isNotEmpty()) {
                        Surface(
                            color = if (tag.contains("hanhphuc")) Color(0xFFE0F7FA) else Color(0xFFFCE4EC),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = tag,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                color = if (tag.contains("hanhphuc")) Color(0xFF00ACC1) else Color(0xFFD81B60),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}