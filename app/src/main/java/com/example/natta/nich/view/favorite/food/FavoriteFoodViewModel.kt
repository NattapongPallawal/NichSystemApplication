package com.example.natta.nich.view.favorite.food

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel;
import com.example.natta.nich.data.FavFood
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FavoriteFoodViewModel : ViewModel() {
    private var mRootRef = FirebaseDatabase.getInstance().reference
    private var mAuth = FirebaseAuth.getInstance()
    private var favFood = MutableLiveData<ArrayList<Pair<String, FavFood>>>()

    fun getFavFood(): MutableLiveData<ArrayList<Pair<String, FavFood>>> {
        val ref = mRootRef.child("favoriteFood/${mAuth.currentUser!!.uid}")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val fav = arrayListOf<Pair<String,FavFood>>()
                p0.children.forEach {
                    val k = it.key!!
                    val v = it.getValue(FavFood::class.java)!!
                    fav.add(Pair(k,v))
                }
                favFood.value = fav
            }
        })
        return favFood

    }
}
