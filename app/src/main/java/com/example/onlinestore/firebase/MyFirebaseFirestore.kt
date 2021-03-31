package com.example.onlinestore.firebase

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.example.onlinestore.R
import com.example.onlinestore.model.FavoriteProduct
import com.example.onlinestore.model.UserCredentials
import com.example.onlinestore.model.UserOrder
import com.example.onlinestore.ui.main.*
import com.example.onlinestore.ui.settings.UserCredentialsFragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

class MyFirebaseFirestore {

    private val database: FirebaseFirestore = FirebaseFirestore.getInstance()

    companion object{
        var userFavorites: MutableList<FavoriteProduct>? = mutableListOf()

        fun checkIfUserIsAvailable() = FirebaseAuth.getInstance().uid != null

        fun getUserFavoritesFromServer(context: Context){
            try {
                MyFirebaseFirestore().database
                    .collection("users")
                    .document(FirebaseAuth.getInstance().uid!!)
                    .collection("favorites")
                    .get()
                    .addOnSuccessListener {
                        UserCredentialsFragment().dismissProgressDialog()
                        userFavorites = it.toObjects<FavoriteProduct>().toMutableList()

                    }

            }catch (e:java.lang.NullPointerException){
                    Toast.makeText(
                        context,
                        "Couldn't get your favorites please check your internet connection.",
                        Toast.LENGTH_LONG)
                        .show()
            }
        }

        fun writeUserCredentials(context: Context,view: View, user: UserCredentials) {

            val fragment = UserCredentialsFragment()
            fragment.showProgressDialog(context)

            val uid = FirebaseAuth.getInstance().currentUser?.uid
            val  db = MyFirebaseFirestore().getFireStore()
                 db.collection("users").document(uid!!)
                    .set(user).addOnSuccessListener {

                        Snackbar.make(context,
                            view,
                            "Data has been updated successfully.",
                            Snackbar.LENGTH_SHORT).show()

                    }.addOnFailureListener {
                        Snackbar.make(context,
                            view,
                            "Something went wrong: ${it.message}",
                            Snackbar.LENGTH_SHORT).show()
                    }.addOnCompleteListener {
                        fragment.dismissProgressDialog()
                     }

        }

        fun writeCartOrderToServer( activity: FragmentActivity, view: View, userOrder: UserOrder){
            val fragment = UserCredentialsFragment.newInstance()
            fragment.showProgressDialog(view.context)

            val  db = MyFirebaseFirestore().getFireStore()

            //random variable for order id
            //also TODO send order id to email address
            val randomOrderId= List(1){ Random.nextInt(100,5000)}
            val id= randomOrderId[0]

            db.collection("orders")
                .document(id.toString())
                .set(userOrder)
                .addOnSuccessListener {

                Snackbar.make(view,
                    "Order placed successfully. With Order ID $id",
                    Snackbar.LENGTH_SHORT).show()

                    CheckOutViewModel().clearBackStack(activity)
                    CartViewViewModel.cartProducts.removeAll{
                        true
                    }

                    activity.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.container, MainFragment.newInstance())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()


            }.addOnFailureListener {
                Snackbar.make(view,
                    "Something went wrong: ${it.message}",
                    Snackbar.LENGTH_SHORT).show()
                    fragment.dismissProgressDialog()

            }.addOnCompleteListener {

                    fragment.dismissProgressDialog()

            }
        }

        suspend fun getUserCredentials(uid:String?): UserCredentials?{
            val user:UserCredentials?

            return if (uid != null) {
                user = MyFirebaseFirestore().database.collection("users")
                    .document(uid).get().await()?.toObject()
                user
            }else
                null

        }

        fun writeUserFavorite(view:View, id: String, product:FavoriteProduct){
            DetailsFragment.isProductAddedOrRemovedFromFavorites = true
            FavoritesFragment.isFirstTimePopulatingProducts = true

            Snackbar.make(view,
                "Marked as favorite",
                Snackbar.LENGTH_SHORT)
                .show()

            MyFirebaseFirestore().getFireStore()
                .collection("users")
                .document(FirebaseAuth.getInstance().uid!!)
                .collection("favorites")
                .document(id).set(product)/*.addOnSuccessListener {
                    Snackbar.make(view,
                        "Marked as favorite",
                        Snackbar.LENGTH_SHORT)
                        .show()
                }.addOnCompleteListener {
                    Snackbar.make(view,
                        "Marked as favorite",
                        Snackbar.LENGTH_SHORT)
                        .show()
                }*/

        }

        fun deleteUserFavorite(view:View, id: String){
            DetailsFragment.isProductAddedOrRemovedFromFavorites = true
            FavoritesFragment.isFirstTimePopulatingProducts = true

/*            Snackbar.make(view,
                "Removed as favorite",
                Snackbar.LENGTH_SHORT)
                .show()*/

            MyFirebaseFirestore().getFireStore()
                .collection("users")
                .document(FirebaseAuth.getInstance().uid!!)
                .collection("favorites")
                .document(id).delete().addOnSuccessListener {
                /*    Snackbar.make(view,
                        "Removed as favorite",
                        Snackbar.LENGTH_SHORT)
                        .show()*/
                }.addOnCompleteListener {
                   Snackbar.make(view,
                        "Removed as favorite",
                        Snackbar.LENGTH_SHORT)
                        .show()
                }

        }

    }


    fun getFireStore(): FirebaseFirestore {
        return database
    }



}


