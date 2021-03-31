package com.example.onlinestore.ui.main

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.onlinestore.MainActivity
import com.example.onlinestore.R
import com.example.onlinestore.adapters.MainGridViewAdapter
import com.example.onlinestore.databinding.MainFragmentBinding
import com.example.onlinestore.firebase.MyFirebaseFirestore
import com.example.onlinestore.model.Product
import com.example.onlinestore.ui.main.MainViewModel.Companion.products
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.lang.reflect.Type
import java.util.*
import kotlin.Comparator

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
        fun checkInternetStateIfOnline(context: Context):Boolean{

            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.getNetworkCapabilities(cm.activeNetwork)
            if (activeNetwork != null) {
                if (activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    return true
                } else if (activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    return true
                }/*  else if (activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)){
                    return true
                }*/
            }
            return false
        }
    }


    private lateinit var viewModel: MainViewModel
    private lateinit var mainGridViewAdapter: MainGridViewAdapter
    private lateinit var binding: MainFragmentBinding
    private var mInterstitialAd: InterstitialAd? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val json = getJsonDataFromAsset()
        val listOfProducts: Type = object : TypeToken<List<Product>>() {}.type
        products = Gson().fromJson(json, listOfProducts)

        MobileAds.initialize(requireContext()) {
            val adRequest = AdRequest.Builder().build()
            binding.adView.loadAd(adRequest)
        }
        val request = AdRequest.Builder().build()

        InterstitialAd.load(requireContext(),
            "ca-app-pub-3940256099942544/1033173712",
            request,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d("MainFragment", adError.message)
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d("MainFragment", "Ad was loaded.")
                    mInterstitialAd = interstitialAd
                }

            })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentBinding.inflate(layoutInflater)
        MainActivity.setActionBarTitle(requireActivity(), getString(R.string.app_name))

        mainGridViewAdapter = MainGridViewAdapter(requireContext(), products!!)
        binding.rvMain.setHasFixedSize(true)
        binding.rvMain.adapter = mainGridViewAdapter

        if (checkInternetStateIfOnline(requireContext()) && MyFirebaseFirestore.checkIfUserIsAvailable()) {
            MyFirebaseFirestore.getUserFavoritesFromServer(requireContext())
        } else if (DetailsFragment.isProductAddedOrRemovedFromFavorites && MyFirebaseFirestore.checkIfUserIsAvailable())
            MyFirebaseFirestore.getUserFavoritesFromServer(requireContext())

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        viewModel.onProductClick(requireView(), mainGridViewAdapter, requireActivity())

        binding.rvMain.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 50) {
                    binding.fabMainCart.hide()
                }
                if (dy < 0)
                    binding.fabMainCart.show()
            }
        })

        binding.fabMainCart.setOnClickListener {

            // mInterstitialAd?.show(requireActivity())

            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, CartViewFragment.newInstance())
                .addToBackStack(newInstance().javaClass.name)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()
        }
    }

    private fun getJsonDataFromAsset(): String? {
        val jsonString: String
        try {
            jsonString = requireContext().assets.open("ProductData.json")
                .bufferedReader()
                .use{ it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return null
        }
        return jsonString
    }

    private fun sortRVItems(comparator: java.util.Comparator<Product>) {

        Collections.sort(products, comparator)
        mainGridViewAdapter.notifyDataSetChanged()
}

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.Favorites -> {
                requireActivity().supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, FavoritesFragment.newInstance())
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    //.setCustomAnimations(R.animator.slide_in,R.animator.slide_out)
                    .addToBackStack(this.javaClass.name)
                    .commit()
            }
            R.id.A_Z -> {
                val comparatorAtoZ = Comparator<Product> { p1, p2 ->
                    return@Comparator p1.name.compareTo(p2.name)
                }
                sortRVItems(comparatorAtoZ)
                return true
            }
            R.id.Rating -> {
                val comparatorReviewRating = Comparator<Product> { p1, p2 ->
                    return@Comparator p2.review - p1.review
                }
                sortRVItems(comparatorReviewRating)
                return true
            }
            R.id.Price_Low_to_High -> {
                val comparatorPrice = Comparator<Product> { p1, p2 ->
                    return@Comparator p1.price - p2.price
                }
                sortRVItems(comparatorPrice)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
        return false
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.options_menu_items, menu)
        val searchViewItem = menu.findItem(R.id.SearchView)
        val searchView = searchViewItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
//                for (product in products!!) {
//                    if (product.name.contains(query!!, true)) {
//                        Log.i("brrrrr",product.name)
//                    }
//                    else {
//                        Toast.makeText(requireContext(), "No Match found", Toast.LENGTH_LONG).show()
//                    }
//                }
                mainGridViewAdapter.getFilter().filter(query)
                searchView.clearFocus()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                mainGridViewAdapter.getFilter().filter(newText)
                return false
            }

        })
        super.onCreateOptionsMenu(menu, inflater)
    }

}