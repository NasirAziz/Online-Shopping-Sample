package com.example.onlinestore.model

data class UserCredentials(
    val userId:String?,
    val name: String?,
    val address: String?,
    val contact: String?)
{
    constructor():
            this("", "", "", "")
}


