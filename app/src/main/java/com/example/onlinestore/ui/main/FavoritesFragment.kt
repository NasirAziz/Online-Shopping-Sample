package com.example.onlinestore.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import com.example.onlinestore.MainActivity
import com.example.onlinestore.R
import com.example.onlinestore.adapters.MainGridViewAdapter
import com.example.onlinestore.databinding.FavoritesFragmentBinding
import com.example.onlinestore.firebase.MyFirebaseFirestore
import com.google.android.material.snackbar.Snackbar

class FavoritesFragment : Fragment() {

    companion object {
        fun newInstance() = FavoritesFragment()
        var isFirstTimePopulatingProducts = true

    }

    private lateinit var viewModel: FavoritesViewModel
    private lateinit var binding: FavoritesFragmentBinding

/*    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.Sort_By).isVisible = false
        super.onPrepareOptionsMenu(menu)
    }*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)

        binding = FavoritesFragmentBinding.inflate(layoutInflater)

        MainActivity.setActionBarTitle(requireActivity(), getString(R.string.favorites))
        setFragmentResultListener("key") { s: String, bundle: Bundle ->
            Log.i("aabb", bundle.getString("k").toString())
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if(DetailsFragment.isProductAddedOrRemovedFromFavorites) {
            if ( MainFragment.checkInternetStateIfOnline(requireContext())) {
                if(isFirstTimePopulatingProducts && !MyFirebaseFirestore.userFavorites.isNullOrEmpty()) {
                    FavoritesViewModel.fillFavProducts()
                    isFirstTimePopulatingProducts = false
                }
            } else
                Snackbar
                    .make(requireView(),"Please connect to the internet to see your favorites",Snackbar.LENGTH_SHORT)
                    .show()
            // UserCredentialsFragment().showProgressDialog(requireContext())
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(FavoritesViewModel::class.java)

            if ( MainFragment.checkInternetStateIfOnline(requireContext())) {
                if(isFirstTimePopulatingProducts && !MyFirebaseFirestore.userFavorites.isNullOrEmpty()) {
                    FavoritesViewModel.fillFavProducts()
                    isFirstTimePopulatingProducts = false
                }
            } else
                Snackbar
                    .make(requireView(),"Please connect to the internet to see your favorites",Snackbar.LENGTH_SHORT)
                    .show()
               // UserCredentialsFragment().showProgressDialog(requireContext())


        val adapter = MainGridViewAdapter(requireContext(), FavoritesViewModel.favProducts)
        binding.rvFavorites.adapter = adapter

        adapter.setOnItemClickListener(object : MainGridViewAdapter.ClickListener {
            override fun onItemClick(view: View, position: Int, productQuantity: Int) {
                if (view == view.findViewById(R.id.ivAddToCart) as View) {

                    if (productQuantity > 0) {
                        CartViewViewModel.addItemToCart(
                            FavoritesViewModel.favProducts[position],
                            productQuantity
                        )
                        Snackbar.make(
                            view,
                            "${FavoritesViewModel.favProducts[position].name} with quantity $productQuantity added to cart",
                            Snackbar.LENGTH_SHORT
                        )
                            .show()
                    }

                } else {

                    activity?.supportFragmentManager
                        ?.beginTransaction()
                        ?.replace(R.id.container, DetailsFragment.newInstance(position, false))
                        ?.addToBackStack(this.javaClass.name)
                        ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        ?.commit()
                }
            }

        })

       // UserCredentialsFragment().dismissProgressDialog()

    }

}