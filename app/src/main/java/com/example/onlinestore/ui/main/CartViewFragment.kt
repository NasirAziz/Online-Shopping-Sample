package com.example.onlinestore.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.onlinestore.MainActivity
import com.example.onlinestore.R
import com.example.onlinestore.databinding.CartViewFragmentBinding
import kotlin.properties.Delegates

class CartViewFragment : Fragment() {

    companion object {
        fun newInstance() = CartViewFragment()
    }

    private lateinit var viewModel: CartViewViewModel
    private lateinit var binding: CartViewFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding = CartViewFragmentBinding.inflate(layoutInflater)
        MainActivity.setActionBarTitle(requireActivity(), getString(R.string.my_cart))

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(CartViewViewModel::class.java)

        viewModel.setAdapter(requireView())

        CartViewViewModel.grandTotalAmount.observe(viewLifecycleOwner,{
            binding.tvGrandTotal.text = resources.getString(R.string.total_payable_amount, it)

        })

        binding.btnCartProceedToCheckOut.setOnClickListener {
            viewModel.proceedToCheckOut(requireActivity())
        }
    }




}