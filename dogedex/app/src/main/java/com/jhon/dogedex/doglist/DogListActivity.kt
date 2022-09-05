package com.jhon.dogedex.doglist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.jhon.dogedex.Dog
import com.jhon.dogedex.DogDetailActivity
import com.jhon.dogedex.DogDetailActivity.Companion.DOG_KEY
import com.jhon.dogedex.R
import com.jhon.dogedex.api.ApiResponseStatus
import com.jhon.dogedex.databinding.ActivityDogListBinding

class DogListActivity : AppCompatActivity() {

    private val dogListViewModel: DogListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDogListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val loadingWheel = binding.loadingWheel

        val recycler = binding.dogRecycler
        recycler.layoutManager = LinearLayoutManager(this)

        val adapter = DogAdapter()

        adapter.setOnItemClickListener {
            // Pass dog to DogDetailActivity
            val intent = Intent(this, DogDetailActivity::class.java)
            intent.putExtra(DOG_KEY, it)
            startActivity(intent)
        }

        recycler.adapter = adapter

        dogListViewModel.dogList.observe(this) { dogList ->
            adapter.submitList(dogList)
        }

        dogListViewModel.status.observe(this) {
            status ->
            when(status){
                ApiResponseStatus.LOADING -> {
                    // Showing progressbar
                    loadingWheel.visibility = View.VISIBLE
                }
                ApiResponseStatus.ERROR -> {
                    // Hiding progressbar
                    Toast.makeText(this, "Hubo un error al descargar recursos", Toast.LENGTH_LONG).show()
                    loadingWheel.visibility = View.GONE
                }
                ApiResponseStatus.SUCCESS -> {
                    // Hiding progressbar
                    loadingWheel.visibility = View.GONE
                }
                else -> {
                    // Hiding progressbar
                    loadingWheel.visibility = View.GONE
                    Toast.makeText(this, "Estado desconocido", Toast.LENGTH_LONG).show()
                }
            }
        }
    }


}