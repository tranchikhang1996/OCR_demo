package com.example.ocr_demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ScrollView
import androidx.activity.viewModels
import com.example.ocr_demo.databinding.ActivityBenchMarkBinding

class BenchMarkActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityBenchMarkBinding
    private val viewModel: BenchMarkViewModel by viewModels {
        BenchMarkViewModel.Factory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityBenchMarkBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        viewModel.benchMarkLog.observe(this) {
            viewBinding.log.text = it
            viewBinding.logContainer.fullScroll(ScrollView.FOCUS_DOWN)
        }
        setupSpinner()
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
                benchMark(parent.getItemAtPosition(pos) as String)
            }

            override fun onNothingSelected(parent: AdapterView<*>) = Unit
        }
    }

    private fun benchMark(engine: String) {
        viewModel.benchMark(applicationContext, engine)
    }
}