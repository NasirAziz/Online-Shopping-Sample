package com.example.onlinestore.firebase

import androidx.lifecycle.map

enum class AuthenticationState {
    AUTHENTICATED, UNAUTHENTICATED
}

val authenticationStateListener
                = FirebaseUserLiveData().map { user->
    if (user!=null){
        AuthenticationState.AUTHENTICATED
    }else{
        AuthenticationState.UNAUTHENTICATED
    }
}