package com.jhon.dogedex.dogdetail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import coil.load
import com.jhon.dogedex.R
import com.jhon.dogedex.api.ApiResponseStatus
import com.jhon.dogedex.databinding.ActivityDogDetailBinding
import com.jhon.dogedex.model.Dog

class DogDetailActivity : AppCompatActivity() {

    companion object {
        const val DOG_KEY = "key"
        const val IS_RECOGNITION_KEY = "is_recognition"
    }

    private val viewModel: DogDetailViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDogDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dog = intent.extras?.getParcelable<Dog>(DOG_KEY)
        val isRecognition = intent?.extras?.getBoolean(IS_RECOGNITION_KEY, false) ?: false

        if (dog == null) {
            Toast.makeText(
                this,
                resources.getString(R.string.error_showing_dog_not_found),
                Toast.LENGTH_LONG
            ).show()
            finish()
            return
        }
        binding.dogIndex.text = resources.getString(R.string.dog_index_format, dog.index)
        binding.lifeExpectancy.text =
            resources.getString(R.string.dog_life_expectancy_format, dog.lifeExpectancy)
        binding.dogImage.load(dog.imageUrl)

        viewModel.status.observe(this) { status ->
            when (status) {
                is ApiResponseStatus.Error -> {
                    // Hiding progressbar
                    Toast.makeText(this, status.messageId, Toast.LENGTH_LONG)
                        .show()
                    binding.loadingWheel.visibility = View.GONE
                }
                is ApiResponseStatus.Loading -> {
                    // Showing progressbar
                    binding.loadingWheel.visibility = View.VISIBLE
                }
                is ApiResponseStatus.Success -> {
                    // Hiding progressbar
                    binding.loadingWheel.visibility = View.GONE
                    finish()
                }
            }
        }

        binding.closeButton.setOnClickListener {
            if (isRecognition) {
                viewModel.addDogToUser(dog.id)
            } else {
                finish()
            }
        }

        binding.dog = dog
    }
}