package com.example.onlinestore.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.example.onlinestore.firebase.FirebaseUserLiveData

class SettingsViewModel : ViewModel() {

    enum class AuthenticationState {
        AUTHENTICATED, UNAUTHENTICATED
    }

companion object{
    val authenticationStateListener
        = FirebaseUserLiveData().map { user->
            if (user!=null){
                AuthenticationState.AUTHENTICATED
            }else{
                AuthenticationState.UNAUTHENTICATED
        }
    }
}


}