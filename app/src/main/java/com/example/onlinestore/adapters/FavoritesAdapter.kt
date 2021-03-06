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
import com.example.onlinestore.model.Product
import com.shawnlin.numberpicker.NumberPicker


class FavoritesAdapter(private val context: Context, private val products: List<Product>):
    RecyclerView.Adapter<FavoritesAdapter.MyViewHolder>() {

    private var onClickListener: ClickListener? = null


    interface ClickListener {
        fun onItemClick(view: View, position: Int, productQuantity: Int)
    }

    fun setOnItemClickListener(clickListener: ClickListener){
        this.onClickListener = clickListener
    }
    inner class MyViewHolder(view:View):RecyclerView.ViewHolder(view){

        init {
            view.setOnClickListener { v->
                if(v != null){
                    onClickListener?.onItemClick(v,adapterPosition,numPicker.value)
                }
            }
        }

        val productImage: ImageView = view.findViewById(R.id.ivProductImage)
        val productName: TextView = view.findViewById(R.id.tvProductName)
        val productPrice: TextView = view.findViewById(R.id.tvProductPrice)
        val numPicker: NumberPicker = view.findViewById(R.id.numPicker)
        val cart :ImageView = view.findViewById(R.id.ivAddToCart)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater
            .inflate(R.layout.rv_main_grid_item_view, parent, false) as View
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = products[position]

        Glide.with(context)
            .load(
                if (position % 2 == 0)
                    R.drawable.productimage__3_
                else
                    R.drawable.productimage__4_
            )
            .centerCrop()
            .into(holder.productImage)

        holder.productName.text = data.name
        holder.productPrice.text = data.price.toString() + " Rs"
        //holder.productRating.text = data.rate.toString()

        holder.cart.setOnClickListener {
            onClickListener?.onItemClick(it, position, holder.numPicker.value)
        }

    }

    override fun getItemCount(): Int = products.size
}