package com.example.ocr_demo

import android.content.Context
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class TextRecognitionViewModel(context: Context) : ViewModel() {
    private val _result = MutableLiveData<String>()
    val result: LiveData<String> = _result

    fun image2text(engine: String) {
        when (engine) {
            "ML-kit" -> mlKit()
            "Tesseract" -> tesseract()
        }
    }

    private fun mlKit() {}

    private fun tesseract() {
        viewModelScope.launch(Dispatchers.Default) {}
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TextRecognitionViewModel(context) as T
        }
    }
}