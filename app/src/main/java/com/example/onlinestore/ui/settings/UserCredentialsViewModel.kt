package com.example.onlinestore.ui.settings

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.onlinestore.databinding.UserCredentailsFragmentBinding
import com.example.onlinestore.firebase.MyFirebaseFirestore
import com.example.onlinestore.model.UserCredentials
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.coroutineContext

class UserCredentialsViewModel : ViewModel() {
    private val scope = CoroutineScope(Dispatchers.Main)

    val uid = FirebaseAuth.getInstance().currentUser?.uid

     fun updateUI(binding: UserCredentailsFragmentBinding, context: Context){

         val fragment = UserCredentialsFragment.newInstance()
         fragment.showProgressDialog(context)

         val id = FirebaseAuth.getInstance().currentUser?.uid
         var user:UserCredentials? = null

         scope.launch {
             user = MyFirebaseFirestore.getUserCredentials(id)
             fragment.dismissProgressDialog()

             if(user!=null) {
                 binding.etCredentialsAddress.setText(user?.address)
                 binding.etCredentialsContact.setText(user?.contact)
                 binding.etCredentialsName.setText(user?.name)

             }else{

                 Toast.makeText(context,
                     "Something went wrong try again later.",
                     Toast.LENGTH_SHORT).show()
             }
         }
    }

}