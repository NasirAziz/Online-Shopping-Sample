package com.example.onlinestore.ui.main

import android.content.Context
import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import com.example.onlinestore.databinding.CheckOutFragmentBinding
import com.example.onlinestore.firebase.MyFirebaseFirestore
import com.example.onlinestore.model.OrderProduct
import com.example.onlinestore.model.PaymentDetails
import com.example.onlinestore.model.UserCredentials
import com.example.onlinestore.model.UserOrder
import com.example.onlinestore.ui.settings.UserCredentialsFragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class CheckOutViewModel : ViewModel() {

    private var uid = FirebaseAuth.getInstance().uid

    fun checkOutUserDetailsWriteToFirebase(
        binding: CheckOutFragmentBinding,
        context: Context,
        view: View
    ) {

        if (MainFragment.checkInternetStateIfOnline(context)) {

            val name = binding.etCOName.text.toString()
            val address = binding.etCOAddress.text.toString()
            val contact = binding.etCOContact.text.toString()
            uid = FirebaseAuth.getInstance().uid

            val user = UserCredentials(uid!!, name, address, contact)

            val firebase = MyFirebaseFirestore
            firebase.writeUserCredentials(context, view, user)
        } else {
            Snackbar.make(
                view,
                "No internet connection please try again later",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    fun placeOrder(activity: FragmentActivity, view: View, binding: CheckOutFragmentBinding) {
        val firebase = MyFirebaseFirestore
        if (MainFragment.checkInternetStateIfOnline(activity.baseContext)) {

            val name = binding.etCOName.text.toString()
            val address = binding.etCOAddress.text.toString()
            val contact = binding.etCOContact.text.toString()

            val products = CartViewViewModel.cartProducts
            val orderProducts: MutableList<OrderProduct> = mutableListOf()

            for (product in products) {
                orderProducts.add(OrderProduct(product.id, product.quantity))
            }

            //TODO check if COD is selected or JAZZCASH then write UserOrder accordingly
            val payableCash = CartViewViewModel.grandTotalAmount.value
            //////////////////////////////////////////////////////
            //TODO added these lines for payment methods recheck before and after using correct storeID and hash
//            val method: String =
//                if (binding.rbCod.isChecked)
//                    "COD"
//                else //if (binding.rbEasypaysa.isChecked)
//                    "Easypaysa"
//                else
//                    null
            val method = CheckOutFragment.paymentMethod
            val status = CheckOutFragment.paymentStatus.toString()
            val refNum = CheckOutFragment.orderRefNum

            val payment = PaymentDetails(method, status, refNum)
            //////////////////////////////////////////////////////
            val order = UserOrder(name, contact, address, payment, orderProducts)

            firebase.writeCartOrderToServer(activity, view, order)

        } else {
            Snackbar.make(
                view,
                "No internet connection please try again later",
                Snackbar.LENGTH_LONG
            ).show()
        }

    }


    suspend fun updateUIWithCredentials(context: Context, binding: CheckOutFragmentBinding) {
        val fragment = UserCredentialsFragment()
        fragment.showProgressDialog(context)

        uid = FirebaseAuth.getInstance().uid

        val user = MyFirebaseFirestore.getUserCredentials(uid)
        Log.i("aaaa", user.toString())

        if (user != null) {

            binding.etCOName.setText(user.name)
            binding.etCOAddress.setText(user.address)
            binding.etCOContact.setText(user.contact)

            fragment.dismissProgressDialog()

        } else {
            fragment.dismissProgressDialog()
        }
    }

    fun checkFieldsNotEmpty(binding: CheckOutFragmentBinding): Boolean {

        return !binding.etCOName.text.isNullOrEmpty()
                && !binding.etCOAddress.text.isNullOrEmpty()
                && !binding.etCOContact.text.isNullOrEmpty()
                && (binding.rbEasypaysa.isChecked || binding.rbCod.isChecked)
    }


    fun clearBackStack(activity: FragmentActivity) {
        val manager = activity.supportFragmentManager
        // for (count in manager.backStackEntryCount.downTo(0))
        if (manager.backStackEntryCount > 0) {
            Log.i("aaaa", manager.backStackEntryCount.toString())
            //val first: FragmentManager.BackStackEntry = manager.getBackStackEntryAt(0)
            manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            Log.i("aaaa", manager.backStackEntryCount.toString())

        }

    }

}