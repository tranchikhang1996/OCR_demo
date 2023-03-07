package com.example.ocr_demo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class TextRecognitionViewModel() : ViewModel() {
    private val _result = MutableLiveData<String>()
    val result: LiveData<String> = _result
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    fun image2Text(input: InputImage) {
        recognizer.process(input).addOnSuccessListener {
            _result.postValue(it.text)
        }
    }
}