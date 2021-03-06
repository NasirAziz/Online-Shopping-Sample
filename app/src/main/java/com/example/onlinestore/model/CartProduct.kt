package com.example.onlinestore.model

data class CartProduct(
    val id: Int,
    val name:String,
    val price:Int,
    var quantity:Int = 0
)
