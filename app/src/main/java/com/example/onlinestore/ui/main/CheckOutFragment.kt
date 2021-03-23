package com.example.onlinestore.ui.main

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import com.example.onlinestore.MainActivity
import com.example.onlinestore.R
import com.example.onlinestore.databinding.CheckOutFragmentBinding
import com.example.onlinestore.ui.payment.PaymentFragment
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
        const val FRAGMENT_RESULT_KEY = "STATUS"
        const val PAYMENT_STATUS_CODE = "STATUS_CODE"

        var paymentStatus by Delegates.notNull<Int>()
        var orderRefNum: String = "null"

    }

    private lateinit var viewModel: CheckOutViewModel
    private lateinit var binding: CheckOutFragmentBinding
    private var isRememberMeChecked = false

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CheckOutFragmentBinding.inflate(layoutInflater)
        MainActivity.setActionBarTitle(requireActivity(), getString(R.string.check_out))
        binding.webViewEasyPaysa.settings.javaScriptEnabled = true
        Log.i("aaaaResponseCode", "ResponseCode2:")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(CheckOutViewModel::class.java)

        setFragmentResultListener(FRAGMENT_RESULT_KEY) { key: String, bundle: Bundle ->
            Log.i("aaabb", bundle.getString("status").toString())

            //TODO check below conditions when storeId and hash are valid parameters
            val status = bundle.getString("status")
            if (status.equals("failed")) {
                paymentStatus = -1
                orderRefNum = bundle.getString("orderRef")!!
            } else if (status.equals("000")) {
                paymentStatus = 0
                orderRefNum = bundle.getString("orderRef")!!
            }
        }

        if (FirebaseAuth.getInstance().uid != null) {
            CoroutineScope(Dispatchers.Main).launch {
                viewModel.updateUIWithCredentials(requireContext(), binding)
            }
        }

        binding.cbRememberMe.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (FirebaseAuth.getInstance().uid != null) {

                    isRememberMeChecked = true

                } else {
                    val provider = arrayListOf(
                        AuthUI.IdpConfig.EmailBuilder().build(),
                        AuthUI.IdpConfig.GoogleBuilder().build()
                    )

                    startAuthActivityForResult.launch(
                        AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(provider)
                            .build()
                    )
                }
            }
        }

        binding.rg.setOnCheckedChangeListener { rg, checkedId ->
            when (checkedId) {
//                R.id.rbEasypaysa -> {
//                    //binding.webViewEasyPaysa.visibility = View.VISIBLE
//                    //binding.webViewEasyPaysa.postUrl("https://easypay.easypaisa.com.pk/easypay/Index.jsf",)
//                    //startPaymentActivityForResult.launch(Intent(requireContext(),))
//                    requireActivity()
//                        .supportFragmentManager.popBackStack()
////                        .beginTransaction()
////                        .replace(R.id.container, PaymentFragment.newInstance())
////                        .addToBackStack(this.javaClass.name)
////                        .commit()
//                    rg.clearCheck()
//                }
                R.id.rbCod -> {
                    binding.webViewEasyPaysa.visibility = View.GONE
                    binding.webViewEasyPaysa.clearCache(true)
                }
            }
        }

        binding.rbEasypaysa.setOnClickListener {
            if (binding.rbEasypaysa.isChecked)
                requireActivity()
                    .supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, PaymentFragment.newInstance())
                    .addToBackStack(this.javaClass.name)
                    .commit()
        }

        binding.btnPlaceOrder.setOnClickListener {

            if (isRememberMeChecked && paymentStatus == 0) {

                viewModel.checkOutUserDetailsWriteToFirebase(
                    binding,
                    requireContext(),
                    requireView()
                )
                viewModel.placeOrder(MainActivity(), requireView(), binding)


            } else if (!isRememberMeChecked && paymentStatus == 0) {
                //send cart data to server without writing credentials
                viewModel.placeOrder(requireActivity(), requireView(), binding)

            }

        }
    }

    private val startAuthActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val response = IdpResponse.fromResultIntent(result.data)

            if (result.resultCode == Activity.RESULT_OK) {
                isRememberMeChecked = true
                Toast.makeText(
                    requireContext(),
                    "Welcome ${FirebaseAuth.getInstance().currentUser?.displayName}!",
                    Toast.LENGTH_SHORT
                )
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
                    return@registerForActivityResult
                }
                if (response.error!!.errorCode == ErrorCodes.NO_NETWORK) {
                    Log.e("Login", "No Internet Connection")

                    binding.cbRememberMe.isChecked = false
                    isRememberMeChecked = false

                    return@registerForActivityResult
                }
                if (response.error!!.errorCode == ErrorCodes.UNKNOWN_ERROR) {
                    Log.e("Login", "Unknown Error")

                    binding.cbRememberMe.isChecked = false
                    isRememberMeChecked = false

                    return@registerForActivityResult
                }

            }
        }

    private val startPaymentActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            // Check that the result is OK
            Log.i("aaaaResponseCode", "ResponseCode: ${result.resultCode}")
            if (result.resultCode == Activity.RESULT_OK) {
                // Get String data from Intent
                val responseCode: String? = result.data?.getStringExtra("status")
                Log.i("aaaaResponseCode", "ResponseCode: $responseCode")
                if (responseCode == "000") {
                    Toast.makeText(requireContext(), "Payment Success", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(requireContext(), "Payment Failed", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }


}


