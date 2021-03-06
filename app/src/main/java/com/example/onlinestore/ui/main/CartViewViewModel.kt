package com.example.onlinestore.ui.main

import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.onlinestore.R
import com.example.onlinestore.adapters.CartViewAdapter
import com.example.onlinestore.model.CartProduct
import com.example.onlinestore.model.Product

class CartViewViewModel : ViewModel() {

    companion object{

        var grandTotalAmount = MutableLiveData(0)

        var cartProducts: MutableList<CartProduct> = mutableListOf()

        fun addItemToCart(item: Product, quantity: Int){
             var isOldItemIncreased = false

                if( cartProducts.isNotEmpty() ) {
                    if(cartProducts.size > 0) {
                        for (product in cartProducts)
                            if (product.id == item.id) {
                                isOldItemIncreased = true
                                product.quantity += quantity
                                grandTotalAmount.value =
                                    grandTotalAmount.value?.plus(product.price * quantity)

                            }
                    }
                    if(!isOldItemIncreased)
                        addItem(item, quantity)

                }else if(cartProducts.isEmpty())
                    addItem(item, quantity)



        }

        private fun addItem(item: Product, quantity: Int) {
                val product = CartProduct(item.id, item.name, item.price, quantity)
                cartProducts.add(product)
            grandTotalAmount.value = grandTotalAmount.value?.plus(product.price * quantity)

        }

        fun deleteProductFromCart(position: Int){

            if( !cartProducts.isNullOrEmpty() ) {
                grandTotalAmount.value =
                    grandTotalAmount.value?.minus(cartProducts[position].price * cartProducts[position].quantity)
                cartProducts.removeAt(position)
            }
        }

    }

    fun setAdapter(view: View) {
        val adapter = CartViewAdapter(view.context, cartProducts)
        val rv = view.findViewById<RecyclerView>(R.id.rvCartView)
        onProductDelete(adapter)
        rv.adapter = adapter
    }

    private fun onProductDelete(adapter: CartViewAdapter){
        adapter.setOnItemClickListener(object : CartViewAdapter.ClickListener {
            override fun onItemClick(view: View, position: Int) {
                deleteProductFromCart(position)
                adapter.notifyItemRemoved(position)
                adapter.notifyItemRangeChanged(position, cartProducts.size)
            }
        })

    }

    fun proceedToCheckOut(activity: FragmentActivity) {
        if (cartProducts.isNotEmpty()) {
            activity.supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, CheckOutFragment.newInstance())
                    .addToBackStack(CartViewFragment.newInstance().javaClass.name)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
        } else {
            Toast.makeText(
                activity.applicationContext,
                "Please shop item(s) to proceed",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


}