package com.example.onlinestore.ui.settings

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.onlinestore.MainActivity
import com.example.onlinestore.R
import com.example.onlinestore.databinding.UserCredentailsFragmentBinding
import com.example.onlinestore.firebase.MyFirebaseFirestore
import com.example.onlinestore.model.UserCredentials
import com.example.onlinestore.ui.main.MainFragment
import com.google.android.material.snackbar.Snackbar

class UserCredentialsFragment : Fragment() {

    companion object {
        fun newInstance() = UserCredentialsFragment()
    }

    private lateinit var viewModel: UserCredentialsViewModel
    private lateinit var binding: UserCredentailsFragmentBinding
    var dialog: Dialog? = null

//    override fun onPrepareOptionsMenu(menu: Menu) {
//        menu.findItem(R.id.Sort_By).isVisible = false
//        super.onPrepareOptionsMenu(menu)
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)

        binding = UserCredentailsFragmentBinding.inflate(layoutInflater)
        dialog = Dialog(requireContext())
        MainActivity.setActionBarTitle(requireActivity(), getString(R.string.my_info))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(UserCredentialsViewModel::class.java)


        viewModel.updateUI( binding, requireContext() )

        binding.btnCredentialsUpdate.setOnClickListener {
            if(MainFragment.checkInternetStateIfOnline(requireContext())) {
                if (checkFieldsNotEmpty()) {
                    val uid = viewModel.uid

                    val name = binding.etCredentialsName.text.toString()
                    val address = binding.etCredentialsAddress.text.toString()
                    val contact = binding.etCredentialsContact.text.toString()
                    val user = UserCredentials(uid!!, name, address, contact)

                    MyFirebaseFirestore.writeUserCredentials(requireContext(), requireView(), user)
                } else {

                    Snackbar.make(binding.root, "Please fill all fields.", Snackbar.LENGTH_LONG)
                        .show()
                }
            }

        }

    }


    private fun checkFieldsNotEmpty(): Boolean {

        return !binding.etCredentialsName.text.isNullOrEmpty()
                && !binding.etCredentialsAddress.text.isNullOrEmpty()
                && !binding.etCredentialsContact.text.isNullOrEmpty()
    }

    fun showProgressDialog(context: Context){
        dialog = Dialog(context)
        dialog!!.setContentView(R.layout.progress_dialog)
        dialog!!.setCancelable(true)
        dialog!!.show()
    }

    fun dismissProgressDialog(){
        if(dialog!=null)
            dialog!!.dismiss()
    }

}