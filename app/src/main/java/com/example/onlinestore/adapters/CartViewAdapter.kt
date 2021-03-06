package com.example.onlinestore.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.onlinestore.R
import com.example.onlinestore.model.CartProduct
import com.example.onlinestore.model.Product

class CartViewAdapter(private val context: Context, private val cartProducts: List<CartProduct>?):
    RecyclerView.Adapter<CartViewAdapter.MyViewHolder>() {

    var clickListener: ClickListener? = null

    interface ClickListener{
        fun onItemClick(view: View, position: Int)
    }

    fun setOnItemClickListener(clickListener: ClickListener){
        this.clickListener = clickListener
    }

    inner class MyViewHolder(view: View):RecyclerView.ViewHolder(view){
        val image: ImageView = view.findViewById(R.id.ivCartProductImage)
        val deleteIcon: ImageView = view.findViewById(R.id.ivCartDeleteItem)
        val name: TextView = view.findViewById(R.id.tvCartProductName)
        val price: TextView = view.findViewById(R.id.tvCartProductPrice)
        val totalPrice: TextView = view.findViewById(R.id.tvCartProductTotalPrice)
        val quantity: TextView = view.findViewById(R.id.tvCartProductQuantity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater
            .inflate(R.layout.rv_cart_list_item_view, parent, false) as View
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if(cartProducts != null) {
            val data = cartProducts[position]
            Glide.with(context)
                .load(
                    if (position % 2 == 0)
                        R.drawable.productimage__3_
                    else
                        R.drawable.productimage__4_
                )
                .centerCrop()
                .into(holder.image)
            holder.name.text = data.name
            holder.price.text = data.price.toString()
            holder.quantity.text = data.quantity.toString()
            holder.totalPrice.text = (data.price * data.quantity).toString()

            holder.deleteIcon.setOnClickListener {
                clickListener?.onItemClick(it, position)
            }
        }
    }

    override fun getItemCount(): Int = cartProducts?.size!!
}