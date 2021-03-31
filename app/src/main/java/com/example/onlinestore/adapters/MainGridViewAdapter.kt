package com.example.onlinestore.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.onlinestore.R
import com.example.onlinestore.model.Product
import com.shawnlin.numberpicker.NumberPicker

class MainGridViewAdapter(private val context: Context, private val products: List<Product>):
    RecyclerView.Adapter<MainGridViewAdapter.MyViewHolder>() {

    private var onClickListener: ClickListener? = null
    private var filteredProducts: MutableList<Product> = products as MutableList<Product>

    interface ClickListener {
        fun onItemClick(view: View, position: Int, productQuantity: Int)
    }

    fun setOnItemClickListener(clickListener: ClickListener) {
        this.onClickListener = clickListener
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        init {

            view.setOnClickListener { v ->
                if (v != null)
                    onClickListener?.onItemClick(v, adapterPosition, numPicker.value)
            }
        }

        val image: ImageView = view.findViewById(R.id.ivProductImage)
        val productName: TextView = view.findViewById(R.id.tvProductName)
        val productPrice: TextView = view.findViewById(R.id.tvProductPrice)
        val numPicker: NumberPicker = view.findViewById(R.id.numPicker)
        val cart :ImageView = view.findViewById(R.id.ivAddToCart)

        /*        override fun onClick(v: View?) {
            if(v != null){
                onClickListener?.onItemClick(v, position = adapterPosition)
            } //       }*/

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater
                .inflate(R.layout.rv_main_grid_item_view, parent, false) as View
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var data = filteredProducts[position]
        Glide.with(context)
            .load(
                if (position % 2 == 0)
                    R.drawable.productimage__3_
                else
                    R.drawable.productimage__4_
            )
            .centerCrop()
            .into(holder.image)

        holder.productName.text = filteredProducts[position].name
        holder.productPrice.text = filteredProducts[position].price.toString() + " Rs"
        //holder.productRating.text = data.rate.toString()

        holder.cart.setOnClickListener {
            onClickListener?.onItemClick(it, position, holder.numPicker.value)
        }

    }
/*    fun filter(q: String) {
        var query = q
        //items.clear()
        if (query.isEmpty()) {
            //items.addAll(itemsCopy)
        } else {
            query = query.toLowerCase(Locale.ROOT)
//            for (item in itemsCopy) {
//                if (item.name.toLowerCase().contains(text)) {
//                    items.add(item)
//                }
//            }
        }
        notifyDataSetChanged()
    }*/

    /**
     *
     * Returns a filter that can be used to constrain data with a filtering
     * pattern.
     *
     *
     * This method is usually implemented by [Adapter]
     * classes.
     *
     * @return a filter used to constrain data
     */
    fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val charSequenceString = constraint.toString()
                //  Log.i("performFiltering","$constraint,, ")
                if (charSequenceString.isEmpty()) {
                    filteredProducts = products as MutableList<Product>
                    //  Log.i("performFiltering","$constraint,, isempty")
                } else {
                    //  Log.i("performFiltering","$constraint,,nope ")
                    val filteredList: MutableList<Product> = mutableListOf()
                    for (product in products) {
                        if (product.name.contains(charSequenceString, true)) {
                            filteredList.add(product)
                            Log.i("performFiltering", "$constraint,,$filteredList ")
                        }
                        //Log.i("performFiltering","$constraint,before, $filteredProducts")
                        filteredProducts = filteredList
                        // Log.i("performFiltering","$constraint,after,$filteredProducts ")

                    }
                }
                val results = FilterResults()
                results.values = filteredProducts
                //  Log.i("performFiltering","$constraint,result.values,${results.count} ")
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults) {
                filteredProducts = results.values as MutableList<Product>
                //    Log.i("performFilteringresult", "$constraint,results.values, ${results.values}")

                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int = filteredProducts.size
}