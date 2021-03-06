package com.example.onlinestore.ui.main

import androidx.lifecycle.ViewModel
import com.example.onlinestore.firebase.MyFirebaseFirestore
import com.example.onlinestore.model.Product

class FavoritesViewModel : ViewModel() {
companion object{
    val favProducts:MutableList<Product> = mutableListOf()

    fun fillFavProducts(){
        for (product in MyFirebaseFirestore.userFavorites!!)
            for(i in MainViewModel.products!!.indices)
                if (product.productId == MainViewModel.products!![i].id) {
                    //this is the solution to favorites bug that duplicates the products
                    if(!favProducts.contains(MainViewModel.products!![i])) {
                            favProducts.add(MainViewModel.products!![i])
                    }
                }
    }
}



}