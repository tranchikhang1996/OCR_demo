package com.example.ocr_demo

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.googlecode.tesseract.android.TessBaseAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import kotlin.coroutines.resume


data class BenchMarkResult(
    @SerializedName("file") val fileName: String,
    @SerializedName("expected") val truth: String,
    @SerializedName("actual") val actual: String,
    @SerializedName("CER") val cer: Double
) {
    val log = "File: $fileName\nExpected: $truth\nActual: $actual\nCER: $cer\n"
}

class BenchMarkViewModel(context: Context) : ViewModel() {
    private val accuracyBenchMarker = AccuracyBenchMarker()
    private val mlKitRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private val testApi: TessBaseAPI
    private val gson = Gson()
    private val mapType: TypeToken<Map<String, String>> = object : TypeToken<Map<String, String>>() {}
    val benchMarkLog = MutableLiveData<String>()
    private val _benchMarkInfo = StringBuilder()

    init {
        val dataPath: String = File(context.getExternalFilesDir(null), "tesseract").absolutePath
        testApi = TessBaseAPI()
        if (!testApi.init(dataPath, "vie")) {
            testApi.recycle()
        }
    }

    fun benchMark(context: Context, engine: String) {
        viewModelScope.launch {
            when (engine) {
                "ML-kit" -> benchMark(context, engine) { mlKitEngine(context, it) }
                "Tesseract" -> benchMark(context, engine) { tesseractEngine(context, it) }
            }
        }
    }

    private suspend fun mlKitEngine(context: Context, uri: Uri): String {
        return suspendCancellableCoroutine { continuation ->
            val input = InputImage.fromFilePath(context, uri)
            mlKitRecognizer.process(input).continueWith {
                continuation.resume(it.result.text)
            }
        }
    }

    private fun tesseractEngine(context: Context, uri: Uri): String {
        val input = InputImage.fromFilePath(context, uri)
        testApi.setImage(input.bitmapInternal)
        return testApi.utF8Text
    }

    private suspend fun benchMark(context: Context, engineName: String, recognizer: suspend (Uri) -> String) {
        context.getExternalFilesDir(null)?.resolve("data-test")
            ?.listFiles { f -> f.isDirectory }?.forEach { langDir ->
                langDir.listFiles { f -> f.isDirectory }
                    ?.forEach { dir ->
                        val benchMarkResult = benchMark(dir, recognizer)
                        val json = gson.toJson(benchMarkResult)
                        val lang = dir.absolutePath.substringBeforeLast(File.separatorChar).trimEnd(File.separatorChar).substringAfterLast(File.separatorChar)
                        val dirName = dir.absolutePath.trimEnd(File.separatorChar).substringAfterLast(File.separatorChar)
                        context.getExternalFilesDir(null)?.resolve("benchMark")?.let {
                            if (!it.exists()) {
                                it.mkdir()
                            }
                            val fileName = "${engineName}_${lang}_${dirName}.json"
                            saveLog(it.resolve(fileName), json)
                        }
                    }
            }
        showLog("completed!")
    }

    private fun saveLog(file: File?, log: String) {
        file ?: return
        try {
            if(!file.exists()) {
                file.createNewFile()
            }
            FileOutputStream(file).bufferedWriter().use { writer ->
                writer.write(log)
            }
        } catch (throwable: Throwable) {
            Log.d("ERROR", (file.absolutePath) ?: "")
        }
    }

    private fun showLog(message: String) {
        _benchMarkInfo.append(message)
        benchMarkLog.postValue(_benchMarkInfo.toString())
    }

    private suspend fun benchMark(dir: File, recognizer: suspend (Uri) -> String): List<BenchMarkResult> {
        val lang = dir.absolutePath.trimEnd(File.separatorChar).substringBeforeLast(File.separatorChar).trimEnd(File.separatorChar).substringAfterLast(File.separatorChar)
        val dirName = dir.absolutePath.trimEnd(File.separatorChar).substringAfterLast(File.separatorChar)
        showLog("Benchmark $lang $dirName\n")
        val images = dir.resolve("images").listFiles()?.sortedBy { it.name.substringBeforeLast('.') }
        val truth = withContext(Dispatchers.IO) {
            FileInputStream(dir.resolve("text/truth.json")).bufferedReader().use { gson.fromJson(it, mapType) }
        }
        val benchMarkResults = mutableListOf<BenchMarkResult>()
        images?.filter { it.isFile }?.forEach { image ->
            val result = verify(image, truth, recognizer)
            benchMarkResults.add(result)
            Log.d("BENCH_MARK", result.log)
        }

        val acer = benchMarkResults.sumOf { it.cer } / benchMarkResults.size
        showLog("Average CER on $lang $dirName: $acer\n")
        return benchMarkResults
    }

    private suspend fun verify(
        image: File,
        truths: Map<String, String>,
        recognizer: suspend (Uri) -> String
    ): BenchMarkResult {
        val fileName = image.name.substringBeforeLast('.')
        val original = recognizer(image.toUri()).formatText()
        val expected = truths[fileName]?.formatText() ?: throw IllegalStateException()
        val cer = accuracyBenchMarker.calculateCER(original, expected)
        return BenchMarkResult(
            image.absolutePath,
            expected,
            original,
            cer
        )
    }

    override fun onCleared() {
        testApi.recycle()
        super.onCleared()
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return BenchMarkViewModel(context) as T
        }
    }
}