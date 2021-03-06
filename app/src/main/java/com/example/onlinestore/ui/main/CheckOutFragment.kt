package com.example.onlinestore.ui.main

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.onlinestore.MainActivity
import com.example.onlinestore.R
import com.example.onlinestore.databinding.CheckOutFragmentBinding
import com.example.onlinestore.firebase.MyFirebaseFirestore
import com.example.onlinestore.model.UserCredentials
import com.example.onlinestore.ui.settings.SettingsFragment
import com.example.onlinestore.ui.settings.SettingsViewModel.Companion.authenticationStateListener
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class CheckOutFragment : Fragment() {

    companion object {
        fun newInstance() = CheckOutFragment()
    }

    private lateinit var viewModel: CheckOutViewModel
    private lateinit var binding: CheckOutFragmentBinding
    private var isRememberMeChecked = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = CheckOutFragmentBinding.inflate(layoutInflater)

        MainActivity.setActionBarTitle(requireActivity(), getString(R.string.check_out))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(CheckOutViewModel::class.java)

        Log.i("aaaa",FirebaseAuth.getInstance().uid.toString())

        if(FirebaseAuth.getInstance().uid != null){
            CoroutineScope(Dispatchers.Main).launch {
                viewModel.updateUIWithCredentials(requireContext(), binding)

            }
        }

        binding.cbRememberMe.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){

                if(FirebaseAuth.getInstance().uid != null) {

                    isRememberMeChecked = true

                }else{
                    val provider = arrayListOf(
                        AuthUI.IdpConfig.EmailBuilder().build(),
                        AuthUI.IdpConfig.GoogleBuilder().build()
                    )

                    startActivityForResult(
                        AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(provider)
                            .build(), SettingsFragment.SIGN_IN_REQUEST_CODE
                    )

                }
            }
        }


        binding.btnPlaceOrder.setOnClickListener {

            if(isRememberMeChecked){

                viewModel.checkOutUserDetailsWriteToFirebase(binding, requireContext(), requireView())
                viewModel.placeOrder( MainActivity(), requireView(), binding)


            }else{
                //send cart data to server without writing credentials
                viewModel.placeOrder( requireActivity() , requireView(), binding)

            }

        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode== SettingsFragment.SIGN_IN_REQUEST_CODE){

            val response = IdpResponse.fromResultIntent(data)

            if(resultCode== Activity.RESULT_OK){
                isRememberMeChecked = true
                Toast.makeText(requireContext(),
                    "Welcome ${FirebaseAuth.getInstance().currentUser?.displayName}!",
                    Toast.LENGTH_SHORT)
                    .show()

            } else {
                // Sign in failed
                binding.cbRememberMe.isChecked = false
                isRememberMeChecked = false

                if (response == null) {
                    // User pressed back button
                    Log.e("Login", "Login canceled by User")

                    binding.cbRememberMe.isChecked = false
                    isRememberMeChecked = false

                    return
                }
                if (response.error!!.errorCode == ErrorCodes.NO_NETWORK) {
                    Log.e("Login", "No Internet Connection")

                    binding.cbRememberMe.isChecked = false
                    isRememberMeChecked = false

                    return
                }
                if (response.error!!.errorCode == ErrorCodes.UNKNOWN_ERROR) {
                    Log.e("Login", "Unknown Error")

                    binding.cbRememberMe.isChecked = false
                    isRememberMeChecked = false

                    return
                }

            }
        }

    }

}