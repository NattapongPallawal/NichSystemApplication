package com.example.natta.nich.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel;
import android.util.Log
import com.example.natta.nich.data.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OrderViewModel : ViewModel() {
    private var mRootRef = FirebaseDatabase.getInstance().reference
    private var mAuth = FirebaseAuth.getInstance()
    private var mOrder = MutableLiveData<ArrayList<Pair<String, Order>>>()

    fun getOrder(): MutableLiveData<ArrayList<Pair<String, Order>>> {
        val ref = mRootRef.child("order").orderByChild("finish").equalTo(false)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                try {
                    val orderTemp = arrayListOf<Pair<String, Order>>()
                    p0.children.forEach {
                        val key = it.key!!
                        val order = it.getValue(Order::class.java)!!
                        if (order.customerID == mAuth.currentUser!!.uid)
                            orderTemp.add(Pair(key, order))
                        Log.d("checkOrder",order.paymentType)

                    }
                    mOrder.value = orderTemp
                }catch (e:Exception){}

            }

        })

        return mOrder

    }
}
