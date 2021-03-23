package com.example.onlinestore.model

data class UserOrder(
    val name: String,
    val contact: String,
    val address: String,
    val payment: PaymentDetails,
    val products: List<OrderProduct>
)

data class OrderProduct(
    var productId: Int,
    val quantity: Int
)

data class PaymentDetails(
    val method: String,
    val status: String,
    val orderRefNum: String = "COD"
)
