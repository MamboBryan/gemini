package com.bizilabs.play.gemini

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BakingViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    val uiState: StateFlow<UiState> =
        _uiState.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.apiKey
    )

    fun sendPrompt(
        bitmap: Bitmap,
        prompt: String
    ) {
        _uiState.value = UiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = generativeModel.generateContent(
                    content {
                        image(bitmap)
                        text(buildString {
                            append(prompt)
                            append("\n")
                            append("don't return it in markdown format")
                        })
                    }
                )
                response.text?.let { outputContent ->
                    Log.e("shasha", "sendPrompt: $outputContent ")
                    _uiState.value = UiState.Success(outputContent)
                }
            } catch (e: Exception) {
                Log.e("shasha", "sendPrompt: $e ")
                _uiState.value = UiState.Error(e.localizedMessage ?: "")
            }
        }
    }
}