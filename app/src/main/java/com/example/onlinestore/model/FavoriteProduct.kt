package com.example.onlinestore.model

data class FavoriteProduct(val productId:Int, val productName:String)
{
    constructor(): this(-1,"")
}
