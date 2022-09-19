package com.jhon.dogedex.doglist

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.jhon.dogedex.api.ApiResponseStatus
import com.jhon.dogedex.databinding.ActivityDogListBinding
import com.jhon.dogedex.dogdetail.DogDetailComposeActivity

private const val GRID_SPAN_COUNT = 3

class DogListActivity : AppCompatActivity() {

    private val dogListViewModel: DogListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDogListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val loadingWheel = binding.loadingWheel

        val recycler = binding.dogRecycler
        recycler.layoutManager = GridLayoutManager(this, GRID_SPAN_COUNT)

        val adapter = DogAdapter()

        adapter.setOnItemClickListener {
            // Pass dog to DogDetailActivity
            val intent = Intent(this, DogDetailComposeActivity::class.java)
            intent.putExtra(DogDetailComposeActivity.DOG_KEY, it)
            startActivity(intent)
        }

        recycler.adapter = adapter

        dogListViewModel.dogList.observe(this) { dogList ->
            adapter.submitList(dogList)
        }

        dogListViewModel.status.observe(this) { status ->
            when (status) {
                is ApiResponseStatus.Error -> {
                    // Hiding progressbar
                    Toast.makeText(this, status.messageId, Toast.LENGTH_LONG)
                        .show()
                    loadingWheel.visibility = View.GONE
                }
                is ApiResponseStatus.Loading -> {
                    // Showing progressbar
                    loadingWheel.visibility = View.VISIBLE
                }
                is ApiResponseStatus.Success -> {
                    // Hiding progressbar
                    loadingWheel.visibility = View.GONE
                }
            }
        }
    }


}