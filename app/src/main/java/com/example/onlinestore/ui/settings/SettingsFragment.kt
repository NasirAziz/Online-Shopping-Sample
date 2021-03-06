package com.example.onlinestore.ui.settings

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
import androidx.fragment.app.FragmentTransaction
import com.example.onlinestore.MainActivity
import com.example.onlinestore.R

import com.example.onlinestore.databinding.SettingsFragmentBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth

class SettingsFragment : Fragment() {

    companion object {
        fun newInstance() = SettingsFragment()
        const val SIGN_IN_REQUEST_CODE = 1122
    }

    private lateinit var viewModel: SettingsViewModel
    private lateinit var binding: SettingsFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = SettingsFragmentBinding.inflate(layoutInflater)

        MainActivity.setActionBarTitle(requireActivity(), getString(R.string.settings))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)

        observerUserLiveData()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode== SIGN_IN_REQUEST_CODE){

            val response = IdpResponse.fromResultIntent(data)

            if(resultCode==Activity.RESULT_OK){
                Toast.makeText(requireContext(),
                        "Welcome ${FirebaseAuth.getInstance().currentUser?.displayName}!",
                        Toast.LENGTH_SHORT)
                        .show()
                //Activity.RESULT_CANCELED
            }
            else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    Log.e("Login", "Login canceled by User")
                    return
                }
                if (response.error!!.errorCode == ErrorCodes.NO_NETWORK) {
                    Log.e("Login", "No Internet Connection")
                    return
                }
                if (response.error!!.errorCode == ErrorCodes.UNKNOWN_ERROR) {
                    Log.e("Login", "Unknown Error")
                    return
                }
            }
        /*else{
                Log.i("TAG","Sign in not good with ${response?.error?.message}")
                Toast.makeText(requireContext(),
                        "Sorry something went wrong please check your internet connection.",
                        Toast.LENGTH_SHORT)
                        .show()
            }*/
        }

    }

     fun signInFlow() {
        val provider = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(provider)
                .build(), SIGN_IN_REQUEST_CODE
        )
    }

    private fun observerUserLiveData(){
        SettingsViewModel.authenticationStateListener.observe(viewLifecycleOwner,
                { authState->
            when(authState!!){
                SettingsViewModel.AuthenticationState.AUTHENTICATED->{

                    binding.btnSettingsLogin.text = getString(R.string.logout)

                    binding.btnSettingsLogin.setOnClickListener {

                        FirebaseAuth.getInstance().signOut()

                        Toast.makeText(requireContext(),
                                "You logged out successfully",Toast.LENGTH_SHORT)
                                .show()
                    }

                    binding.clPersonalInfo.setOnClickListener {
                        showUserCredentials()
                    }
                }

                SettingsViewModel.AuthenticationState.UNAUTHENTICATED->{
                    binding.btnSettingsLogin.text = getString(R.string.login)
                    binding.btnSettingsLogin.setOnClickListener {
                        signInFlow()
                    }
                    binding.clPersonalInfo.setOnClickListener {
                        Toast.makeText(
                                requireContext(),
                                "Please sign in to see your credentials",
                                Toast.LENGTH_SHORT)
                                .show()
                    }

                }
            }
        })
    }

    private fun showUserCredentials() {
        activity?.supportFragmentManager
                ?.beginTransaction()
                ?.addToBackStack(newInstance().javaClass.name)
                ?.replace(R.id.container,
                        UserCredentialsFragment.newInstance())
                ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                ?.commit()
    }


}