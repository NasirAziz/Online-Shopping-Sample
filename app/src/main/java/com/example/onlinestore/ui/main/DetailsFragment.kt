package com.example.onlinestore.ui.main

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.onlinestore.MainActivity
import com.example.onlinestore.R
import com.example.onlinestore.databinding.DetailsFragmentBinding
import com.example.onlinestore.firebase.MyFirebaseFirestore
import com.example.onlinestore.firebase.MyFirebaseFirestore.Companion.checkIfUserIsAvailable
import com.example.onlinestore.model.FavoriteProduct
import com.example.onlinestore.model.Product
import com.google.android.material.snackbar.Snackbar
import kotlin.properties.Delegates

class DetailsFragment : Fragment() {

    companion object {
        var isProductAddedOrRemovedFromFavorites:Boolean = false
        lateinit var products: List<Product>

        @JvmStatic
        fun newInstance(position: Int,isMainFragment: Boolean) = DetailsFragment().apply {
            arguments = Bundle().apply {
                putInt("position", position)
                putBoolean("isMainFragment", isMainFragment)
            }
        }
    }

    private lateinit var viewModel: DetailsViewModel
    private lateinit var binding: DetailsFragmentBinding
    private var position by Delegates.notNull<Int>()
    private var isMainFragment by Delegates.notNull<Boolean>()
    private var isFavorite = false


    override fun onAttach(context: Context) {
        super.onAttach(context)
        isMainFragment = requireArguments().getBoolean("isMainFragment")
        products = if(isMainFragment)
            MainViewModel.products!!
        else
            FavoritesViewModel.favProducts
        Log.i("zzzzz",products.sortedBy { it.rate }.toString())

        position = requireArguments().getInt("position")

    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = DetailsFragmentBinding.inflate(layoutInflater)

        if(checkIfUserIsAvailable() && MainFragment.checkInternetStateIfOnline(requireContext())) {
            if ( isMainFragment ) {
                if (MyFirebaseFirestore.userFavorites != null)
                    for (product in MyFirebaseFirestore.userFavorites!!) {
                        if (products[position].id == product.productId) {
                            isFavorite = true
                            binding.ivDetailsFavorite.background =
                                ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.ic_favorite_24
                                )
                            // binding.ivDetailsFavorite.setColorFilter(ContextCompat.getColor(requireContext(),R.color.color_red))

                        }
                    }
            }else {
                isFavorite = true
                binding.ivDetailsFavorite.background =
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_favorite_24
                    )
            }
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(DetailsViewModel::class.java)
        MainActivity.setActionBarTitle(requireActivity(),products[position].name)

        viewModel.setUpUI(requireView(), binding, position)

        binding.ivDetailAddToCart.setOnClickListener {

            val quantity = binding.numPickerDetails.value
            if (quantity > 0) {
                CartViewViewModel.addItemToCart(products[position], quantity)
                Snackbar.make(requireView(),
                    "${products[position].name} with quantity $quantity added to cart",
                    Snackbar.LENGTH_SHORT)
                    .show()
            }

        }
        binding.ivDetailsFavorite.setOnClickListener {
            if (MainFragment.checkInternetStateIfOnline(requireContext()) && checkIfUserIsAvailable()) {
               if(isMainFragment) {
                    if (isFavorite) {

                        MyFirebaseFirestore.deleteUserFavorite(
                            requireView(),
                            products[position].id.toString()
                        )

                        binding.ivDetailsFavorite.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_favorite_border_24
                        )
                        isFavorite = false

                    } else if (!isFavorite) {
                        val favProduct =
                            FavoriteProduct(products[position].id, products[position].name)

                        MyFirebaseFirestore.writeUserFavorite(
                            requireView(),
                            products[position].id.toString(),
                            favProduct
                        )
                        binding.ivDetailsFavorite.background =
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_favorite_24)
                        isFavorite = true
                    }
               }else if(!isMainFragment){

                    MyFirebaseFirestore.deleteUserFavorite(
                       requireView(),
                       products[position].id.toString()
                   )
                   binding.ivDetailsFavorite.background = ContextCompat.getDrawable(
                       requireContext(),
                       R.drawable.ic_favorite_border_24
                   )
                   //TODO check this line of code
                   //this is the solution to favorite's bug
                   FavoritesViewModel.favProducts.removeAt(position)
                   MyFirebaseFirestore.userFavorites?.removeAt(position)

               }
            }
            else{
                Snackbar.make(requireView(),
                    "Please login or check your internet connection",
                    Snackbar.LENGTH_SHORT)
                    .show()
            }
        }

    }


}
