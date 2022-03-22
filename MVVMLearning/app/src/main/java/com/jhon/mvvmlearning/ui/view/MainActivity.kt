package com.jhon.mvvmlearning.ui.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.jhon.mvvmlearning.databinding.ActivityMainBinding
import com.jhon.mvvmlearning.ui.viewmodel.QuoteViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val quoteViewModel: QuoteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        quoteViewModel.onCreate()

        quoteViewModel.quoteModel.observe(this, Observer { currentQuote ->
            binding.tvQuote.text = currentQuote.quote
            binding.tvAuthor.text = currentQuote.author
        })

        // Progress
        quoteViewModel.isLoading.observe(this, Observer {
            binding.progress.isVisible = it
        })

        binding.viewContainer.setOnClickListener {
            quoteViewModel.randomQuote()
        }
    }
}