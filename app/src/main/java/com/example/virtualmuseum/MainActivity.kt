package com.example.virtualmuseum

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.virtualmuseum.ui.theme.VirtualMuseumTheme
import com.example.virtualmuseum.ui.navigation.Navigation
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme

class MainActivity : AppCompatActivity() {
    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VirtualMuseumTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navigation() // <-- Gọi NavHost ở đây
                }
            }
        }
    }
}