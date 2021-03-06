package com.example.onlinestore.ui.main

import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModel
import com.example.onlinestore.R
import com.example.onlinestore.adapters.MainGridViewAdapter
import com.example.onlinestore.model.Product
import com.google.android.material.snackbar.Snackbar

class MainViewModel : ViewModel() {

    companion object {
        var products: List<Product>? = listOf()
    }

    fun onProductClick(v: View, gridViewAdapter: MainGridViewAdapter, activity: FragmentActivity) {

        gridViewAdapter.setOnItemClickListener(object : MainGridViewAdapter.ClickListener {
            override fun onItemClick(view: View, position: Int, productQuantity: Int) {

                if (view == view.findViewById(R.id.ivAddToCart) as View) {

                    if (productQuantity > 0) {
                        CartViewViewModel.addItemToCart(products!![position], productQuantity)
                        Snackbar.make(
                            v,
                            "${products!![position].name} with quantity $productQuantity added to cart",
                            Snackbar.LENGTH_SHORT
                        )
                            .show()
                    }

                } else {

                    activity.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.container, DetailsFragment.newInstance(position, true))
                        .addToBackStack(MainFragment.newInstance().javaClass.name)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
            }
        })
    }


}