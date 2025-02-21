package com.bizilabs.play.gemini

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.bizilabs.play.gemini.screeen.GeminiScreen
import com.bizilabs.play.gemini.screeen.GeminiScreenViewModel
import com.bizilabs.play.gemini.ui.theme.GeminiTheme

class MainActivity : ComponentActivity() {
    private val viewModel: GeminiScreenViewModel = GeminiScreenViewModel(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GeminiTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    GeminiScreen(viewModel = viewModel)
                }
            }
        }
    }
}