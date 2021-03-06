package com.example.onlinestore.ui.main

import android.text.method.ScrollingMovementMethod
import android.view.View
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.example.onlinestore.R
import com.example.onlinestore.databinding.DetailsFragmentBinding

class DetailsViewModel : ViewModel() {

    fun setUpUI(view: View,binding: DetailsFragmentBinding, position: Int){

       binding.tvDetailsProductName.text = DetailsFragment.products[position].name

        ("Rating: " + DetailsFragment.products[position].review).also {
           binding.tvDetailsRating.text = it
        }

        binding.tvDetailsDescription.movementMethod = ScrollingMovementMethod()
        binding.tvDetailsDescription.text = DetailsFragment.products[position].content
        binding.tvDetailsPrice.text = DetailsFragment.products[position].price.toString()

        Glide.with(view.context)
                .load(
                        if (position % 2 == 0)
                            R.drawable.productimage__3_
                        else
                            R.drawable.productimage__4_
                )
                .centerCrop()
                .into(binding.ivDetailsProductImage)
    }




}