package com.example.ocr_demo

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.googlecode.tesseract.android.TessBaseAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


class TextRecognitionViewModel : ViewModel() {
    private val _result = MutableLiveData<String>()
    val result: LiveData<String> = _result
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    fun image2Text(input: InputImage) {
        recognizer.process(input).addOnSuccessListener {
            _result.postValue(it.text)
        }
    }

    fun image2TextTess(context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val dataPath: String = File(context.getExternalFilesDir(null), "tesseract").absolutePath
                val tess = TessBaseAPI()
                if (!tess.init(dataPath, "vie")) {
                    tess.recycle()
                    return@launch
                }
                tess.setImage(createBitMap(context, uri))
                tess.utF8Text?.let { _result.postValue(it) }
                tess.recycle()
            } catch (throwable: Throwable) {
                Log.d("OCR_DEMO", throwable.message ?: "")
            }
        }
    }
}