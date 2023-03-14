package com.example.ocr_demo

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.googlecode.tesseract.android.TessBaseAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


class TextRecognitionViewModel(context: Context) : ViewModel() {
    private val _result = MutableLiveData<String>()
    val result: LiveData<String> = _result
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private val testApi: TessBaseAPI

    init {
        val dataPath: String = File(context.getExternalFilesDir(null), "tesseract").absolutePath
        testApi = TessBaseAPI()
        if (!testApi.init(dataPath, "vie")) {
            testApi.recycle()
        }
    }

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
            try {
                testApi.setImage(inputImage.bitmapInternal)
                testApi.utF8Text?.let { _result.postValue(it) }
            } catch (throwable: Throwable) {
                Log.d("ERROR", throwable.message ?: "")
            }
        }
    }

    override fun onCleared() {
        testApi.recycle()
        super.onCleared()
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TextRecognitionViewModel(context) as T
        }
    }
}