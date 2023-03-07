package com.example.ocr_demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.example.ocr_demo.databinding.ActivityResultBinding
import com.google.mlkit.vision.common.InputImage

class ResultActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityResultBinding
    private val textRecViewModelModel: TextRecognitionViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityResultBinding.inflate(layoutInflater)
        textRecViewModelModel.result.observe(this) {
            viewBinding.text.text = it
        }
        setContentView(viewBinding.root)
        showImage()
        parseImage()
    }

    private fun showImage() {
        intent.data?.let {
            Glide.with(this)
                .load(it)
                .signature(ObjectKey(System.currentTimeMillis()))
                .into(viewBinding.image)
        }
    }

    private fun parseImage() {
        intent.data?.let {
            val input = InputImage.fromFilePath(applicationContext, it)
            textRecViewModelModel.image2Text(input)
        }
    }
}