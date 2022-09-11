package com.jhon.dogedex

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import coil.load
import com.jhon.dogedex.databinding.ActivityDogDetailBinding
import com.jhon.dogedex.model.Dog

class DogDetailActivity : AppCompatActivity() {

    companion object {
        const val DOG_KEY = "key"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDogDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dog = intent.extras?.getParcelable<Dog>(DOG_KEY)

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
        binding.closeButton.setOnClickListener {
            finish()
        }

        binding.dog = dog
    }
}