package com.example.onlinestore.model

data class UserOrder(val name:String,
                     val contact: String,
                     val address:String,
                     val payableCash:String,
                     val products:List<OrderProduct>)

data class OrderProduct (
    var productId: Int,
    val quantity: Int)
