package com.example.natta.nich.view.favorite.restaurant

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel;
import com.example.natta.nich.data.FavRes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FavoriteRestaurantViewModel : ViewModel() {
    private var mRootRef = FirebaseDatabase.getInstance().reference
    private var mAuth = FirebaseAuth.getInstance()
    private var favRes = MutableLiveData<ArrayList<Pair<String,FavRes>>>()

    fun getFavRes(): MutableLiveData<ArrayList<Pair<String, FavRes>>> {
        val ref = mRootRef.child("favoriteRestaurant/${mAuth.currentUser!!.uid}")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val fav = arrayListOf<Pair<String, FavRes>>()
                p0.children.forEach {
                    val k = it.key!!
                    val v = it.getValue(FavRes::class.java)!!
                    fav.add(Pair(k,v))
                }
                favRes.value = fav
            }
        })
        return favRes

    }

}
