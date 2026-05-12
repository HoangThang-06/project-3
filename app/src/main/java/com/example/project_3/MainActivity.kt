package com.example.project_3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.project_3.ui.auth.LoginScreen
import com.example.project_3.ui.theme.Project3Theme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {

            Project3Theme {

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) {

                    LoginScreen()

                }
            }
        }
    }
}