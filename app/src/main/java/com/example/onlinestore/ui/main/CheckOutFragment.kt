package com.example.onlinestore.ui.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.onlinestore.JazzPaymentActivity
import com.example.onlinestore.MainActivity
import com.example.onlinestore.R
import com.example.onlinestore.databinding.CheckOutFragmentBinding
import com.example.onlinestore.firebase.MyFirebaseFirestore
import com.example.onlinestore.model.UserCredentials
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.properties.Delegates


class CheckOutFragment : Fragment() {

    companion object {
        fun newInstance() = CheckOutFragment()

        //        const val FRAGMENT_RESULT_KEY = "STATUS"
//        const val PAYMENT_STATUS_CODE = "STATUS_CODE"
        const val KEY_PRICE = "PRICE"
        const val KEY_ORDER_REF_NO = "ORDER_REF_NO"

        private const val PAYMENT_STATUS_SUCCESS = 0
        private const val PAYMENT_STATUS_FAILED = -1
        private const val PAYMENT_STATUS_COD = 1
        private const val PAYMENT_METHOD_COD = "COD"
        private const val PAYMENT_METHOD_JAZZ = "JAZZ"

        lateinit var paymentMethod: String
        var paymentStatus by Delegates.notNull<Int>()
        var orderRefNum: String = "null"

    }

    private lateinit var viewModel: CheckOutViewModel
    private lateinit var binding: CheckOutFragmentBinding
    private var isRememberMeChecked = false

//    override fun onPrepareOptionsMenu(menu: Menu) {
//        menu.findItem(R.id.Sort_By).isVisible = false
//        super.onPrepareOptionsMenu(menu)
//    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //  setHasOptionsMenu(true)

        binding = CheckOutFragmentBinding.inflate(layoutInflater)
        MainActivity.setActionBarTitle(requireActivity(), getString(R.string.check_out))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(CheckOutViewModel::class.java)

/*        setFragmentResultListener(FRAGMENT_RESULT_KEY) { key: String, bundle: Bundle ->
            Log.i("aaabb", bundle.getString("status").toString())

            //check below conditions when storeId and hash are valid parameters
            val status = bundle.getString("status")
            if (status.equals("failed")) {
                paymentStatus = -1
                orderRefNum = bundle.getString("orderRef")!!
            } else if (status.equals("000")) {
                paymentStatus = 0
                orderRefNum = bundle.getString("orderRef")!!
            }
        }*/

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
                R.id.rbEasypaysa -> {
                    paymentMethod = PAYMENT_METHOD_JAZZ

                    val intent = Intent(requireContext(), JazzPaymentActivity::class.java)
                    val amount = CartViewViewModel.grandTotalAmount.value
                    intent.putExtra(KEY_PRICE, amount)
                    startJazzPaymentActivityForResult.launch(intent)

                }
                R.id.rbCod -> {
                    paymentMethod = PAYMENT_METHOD_COD
                    paymentStatus = PAYMENT_STATUS_COD
                }
            }
        }

        binding.btnPlaceOrder.setOnClickListener {
            if (viewModel.checkFieldsNotEmpty(binding)) {
                if (isRememberMeChecked) {
                    checkOutFlow(true)
                } else if (!isRememberMeChecked) {
                    checkOutFlow(false)
                }
            } else {
                Snackbar.make(
                    view,
                    "Please fill the required fields.",
                    Snackbar.LENGTH_LONG
                ).show()
            }

        }
    }

    private fun checkOutFlow(isRememberMeChecked: Boolean) {
        if (paymentMethod == PAYMENT_METHOD_JAZZ && paymentStatus == PAYMENT_STATUS_SUCCESS) {
            //send cart data to server without writing credentials
            if (isRememberMeChecked) {
                val uid = FirebaseAuth.getInstance().uid
                val name = binding.etCOName.text.toString()
                val address = binding.etCOAddress.text.toString()
                val contact = binding.etCOContact.text.toString()
                val user = UserCredentials(uid, name, address, contact)

                MyFirebaseFirestore.writeUserCredentials(requireContext(), requireView(), user)
            }
            viewModel.placeOrder(requireActivity(), requireView(), binding)
        } else if (paymentMethod == PAYMENT_METHOD_JAZZ && paymentStatus == PAYMENT_STATUS_FAILED) {
            Toast.makeText(
                requireContext(),
                "Payment was Failed try again",
                Toast.LENGTH_SHORT
            )
                .show()
        } else if (paymentMethod == PAYMENT_METHOD_COD && paymentStatus == PAYMENT_STATUS_COD) {
            //send cart data to server without writing credentials
            if (isRememberMeChecked) {
                val uid = FirebaseAuth.getInstance().uid
                val name = binding.etCOName.text.toString()
                val address = binding.etCOAddress.text.toString()
                val contact = binding.etCOContact.text.toString()
                val user = UserCredentials(uid, name, address, contact)

                MyFirebaseFirestore.writeUserCredentials(requireContext(), requireView(), user)
            }
            viewModel.placeOrder(requireActivity(), requireView(), binding)
        } else {
            Toast.makeText(
                requireContext(),
                "Something went wrong please try again",
                Toast.LENGTH_SHORT
            )
                .show()
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

    private val startJazzPaymentActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            // Check that the result is OK
            Log.i("sssss", result.resultCode.toString())
            if (result.resultCode == Activity.RESULT_OK) {
                // Get String data from Intent
                val responseCode: String? = result.data?.getStringExtra("status")
                if (responseCode == "000") {
                    orderRefNum = result.data?.getStringExtra(KEY_ORDER_REF_NO).toString()
                    paymentStatus = PAYMENT_STATUS_SUCCESS
                    paymentMethod = PAYMENT_METHOD_JAZZ
                    orderRefNum
                    Toast.makeText(requireContext(), "Payment Success", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    paymentStatus = PAYMENT_STATUS_FAILED
                    Toast.makeText(requireContext(), "Payment Failed", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Log.i("sssss", result.resultCode.toString())
                paymentMethod = PAYMENT_METHOD_JAZZ
                paymentStatus = PAYMENT_STATUS_FAILED
            }
        }


}


