package com.example.onlinestore

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.onlinestore.databinding.MainActivityBinding
import com.example.onlinestore.ui.main.FavoritesFragment
import com.example.onlinestore.ui.main.MainFragment
import com.example.onlinestore.ui.main.MainViewModel
import com.example.onlinestore.ui.settings.SettingsFragment


class MainActivity : AppCompatActivity() {
   private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = MainActivityBinding.inflate(layoutInflater)

        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menu?.add(FAVORITES)
//        menu?.add(ACCOUNTS)
        val inflate = MenuInflater(this)
        inflate.inflate(R.menu.options_menu_items,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){

            R.id.Favorites -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(
                        R.id.container,
                        FavoritesFragment.newInstance()
                    )
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack(MainFragment.newInstance().javaClass.name)
                    .commit()

            }
            R.id.Account -> {

                supportFragmentManager
                    .beginTransaction()
                    .replace(
                        R.id.container,
                        SettingsFragment.newInstance()
                    )
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(MainFragment.newInstance().javaClass.name)
                    .commit()
            }
        }


        return super.onOptionsItemSelected(item)
    }


    companion object{

        private fun networkStatePermissionGranted(context: Context): Boolean {
            return when (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_NETWORK_STATE)) {
                PackageManager.PERMISSION_GRANTED -> true
                else -> false
            }
        }


        fun clearBackStack() {
            val manager = MainActivity().supportFragmentManager
            for (count in manager.backStackEntryCount.downTo(0)) {
                Log.i("aaaa", manager.backStackEntryCount.toString() + " $count")
                if (manager.backStackEntryCount >= 0) {
                    Log.i("aaaa", manager.backStackEntryCount.toString() + " $count")
                    val first: FragmentManager.BackStackEntry =
                        manager.getBackStackEntryAt(count )
                    manager.popBackStack(first.id, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                }
            }
        }

        fun setActionBarTitle(activity: FragmentActivity, title: String){
            val actionBar =  (activity as AppCompatActivity).supportActionBar
            actionBar?.title = title
        }

/*
        fun checkAndRequestPermissions(context: Context):Boolean{
            var result = false
            Dexter.withContext(context).withPermissions(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.INTERNET,
                android.Manifest.permission.ACCESS_NETWORK_STATE
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                    result = true
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    result = false
                }
            }).check()

            return result
        }
*/
    }
}