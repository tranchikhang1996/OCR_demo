package com.example.ocr_demo

import android.content.Context
import androidx.lifecycle.*
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class TextRecognitionViewModel(context: Context) : ViewModel() {
    private val _result = MutableLiveData<String>()
    val result: LiveData<String> = _result
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    fun image2text(input: InputImage, engine: String) {
        when (engine) {
            "ML-kit" -> mlKit(input)
            "Tesseract" -> tesseract(input)
        }
    }

    private fun mlKit(input: InputImage) {
        recognizer.process(input).addOnSuccessListener {
            _result.postValue(it.text)
        }
    }

    private fun tesseract(inputImage: InputImage) {
        viewModelScope.launch(Dispatchers.Default) {
        }
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TextRecognitionViewModel(context) as T
        }
    }
}