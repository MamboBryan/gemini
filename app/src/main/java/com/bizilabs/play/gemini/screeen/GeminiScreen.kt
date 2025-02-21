package com.bizilabs.play.gemini.screeen

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun GeminiScreen(viewModel: GeminiScreenViewModel) {
    val state by viewModel.state.collectAsState()
    GeminiScreenContent(
        state = state,
        onValueChangePrompt = viewModel::updatePrompt,
        onClickSendPrompt = viewModel::onClickSendPrompt,
        onValueChangeUri = viewModel::updateImage
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeminiScreenContent(
    state: GeminiScreenState,
    onValueChangeUri: (Uri) -> Unit,
    onValueChangePrompt: (String) -> Unit,
    onClickSendPrompt: () -> Unit,
) {

    val context = LocalContext.current

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            onValueChangeUri(uri)
        } else {
            Toast.makeText(context, "Image selection canceled", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Gemini")
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            AnimatedContent(targetState = state.current) { ui ->
                when (ui) {
                    is GeminiScreenUiState.Error -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .background(androidx.compose.ui.graphics.Color.Red),
                                text = ui.message
                            )
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            ) {
                                AnimatedContent(
                                    targetState = state.image
                                ) { image ->
                                    when (image) {
                                        null -> {
                                            Column(
                                                modifier = Modifier.fillMaxSize(),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Text(text = "Uplaod Image")
                                                Button(
                                                    modifier = Modifier.padding(top = 24.dp),
                                                    onClick = { imagePickerLauncher.launch("image/*") }
                                                ) {
                                                    Text(text = "Upload")
                                                }
                                            }
                                        }

                                        else -> {
                                            AsyncImage(
                                                modifier = Modifier.fillMaxSize(),
                                                model = image,
                                                contentDescription = "hakuna shida"
                                            )
                                        }
                                    }
                                }
                            }
                            Column(modifier = Modifier.fillMaxWidth()) {
                                TextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = state.prompt,
                                    onValueChange = onValueChangePrompt,
                                    trailingIcon = {
                                        Button(
                                            onClick = onClickSendPrompt,
                                            enabled = state.isButtonEnabled
                                        ) {
                                            Text(text = "Send")
                                        }
                                    }
                                )
                            }

                        }
                    }

                    GeminiScreenUiState.Idle -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            ) {
                                AnimatedContent(
                                    targetState = state.image
                                ) { image ->
                                    when (image) {
                                        null -> {
                                            Column(
                                                modifier = Modifier.fillMaxSize(),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Text(text = "Uplaod Image")
                                                Button(
                                                    modifier = Modifier.padding(top = 24.dp),
                                                    onClick = { imagePickerLauncher.launch("image/*") }
                                                ) {
                                                    Text(text = "Upload")
                                                }
                                            }
                                        }

                                        else -> {
                                            AsyncImage(
                                                modifier = Modifier.fillMaxSize(),
                                                model = image,
                                                contentDescription = "hakuna shida"
                                            )
                                        }
                                    }
                                }
                            }
                            Column(modifier = Modifier.fillMaxWidth()) {
                                TextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = state.prompt,
                                    onValueChange = onValueChangePrompt,
                                    trailingIcon = {
                                        Button(
                                            onClick = onClickSendPrompt,
                                            enabled = state.isButtonEnabled
                                        ) {
                                            Text(text = "Send")
                                        }
                                    }
                                )
                            }

                        }
                    }

                    GeminiScreenUiState.Loading -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is GeminiScreenUiState.Success -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(modifier = Modifier.padding(16.dp), text = ui.response)
                        }
                    }
                }
            }

        }
    }

}

// Helper to create a file for the image
fun createImageFile(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
    return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
}