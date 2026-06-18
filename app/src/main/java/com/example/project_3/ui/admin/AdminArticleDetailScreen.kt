package com.example.project_3.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.project_3.data.model.Article
import com.example.project_3.viewmodel.AdminManageArticleViewModel

@Composable
fun AdminArticleDetailScreen(
    article: Article,
    navController: NavController,
    viewModel: AdminManageArticleViewModel
) {
    Scaffold { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            Text(text = "Chi tiết bài viết của: ${article.author_name}", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = article.content)

            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = { navController.popBackStack() }) {
                Text("Quay lại")
            }
        }
    }
}