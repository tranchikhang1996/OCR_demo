package com.example.ocr_demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.example.ocr_demo.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityResultBinding
    private val viewModel: TextRecognitionViewModel by viewModels {
        TextRecognitionViewModel.Factory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        showImage()
        setupSpinner()
        viewModel.result.observe(this) {
            viewBinding.loading.isVisible = false
            viewBinding.text.text = it
        }
    }

    private fun setupSpinner() {
        ArrayAdapter.createFromResource(
            this,
            R.array.engines_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            viewBinding.engines.adapter = adapter
        }

        viewBinding.engines.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                parseImage(parent.getItemAtPosition(pos) as String)
            }

            override fun onNothingSelected(parent: AdapterView<*>) = Unit
        }
    }

    private fun showImage() {
        intent.data?.let {
            Glide.with(this)
                .load(it)
                .signature(ObjectKey(System.currentTimeMillis()))
                .into(viewBinding.image)
        }
    }

    private fun parseImage(engine: String) {
        viewBinding.loading.isVisible = true
        viewBinding.text.text = ""
        viewBinding.loading.isVisible = false
    }
}