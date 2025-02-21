package com.bizilabs.play.gemini.screeen

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bizilabs.play.gemini.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface GeminiScreenUiState {
    object Idle : GeminiScreenUiState
    object Loading : GeminiScreenUiState
    data class Success(val response: String) : GeminiScreenUiState
    data class Error(val message: String) : GeminiScreenUiState
}

data class GeminiScreenState(
    val image: Uri? = null,
    val prompt: String = "",
    val current: GeminiScreenUiState = GeminiScreenUiState.Idle
) {
    val isButtonEnabled: Boolean
        get() = image != null && prompt.isNotBlank()
}

class GeminiScreenViewModel(
    val context: Context,
) : ViewModel() {

    private val _state = MutableStateFlow(GeminiScreenState())
    val state get() = _state.asStateFlow()

    fun updatePrompt(value: String) {
        _state.update { it.copy(prompt = value) }
    }

    fun updateImage(value: Uri) {
        _state.update { it.copy(image = value) }
    }

    fun onClickSendPrompt() {
        val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, state.value.image);
        sendPrompt(
            bitmap = bitmap,
            prompt = state.value.prompt
        )
    }

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.apiKey
    )

    fun sendPrompt(
        bitmap: Bitmap,
        prompt: String
    ) {

        _state.update { it.copy(current = GeminiScreenUiState.Loading) }

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
                    _state.update { it.copy(current = GeminiScreenUiState.Success(outputContent)) }

                }
            } catch (e: Exception) {
                Log.e("shasha", "sendPrompt: $e ")
                _state.update { it.copy(current = GeminiScreenUiState.Error(e.localizedMessage)) }

            }
        }
    }

}