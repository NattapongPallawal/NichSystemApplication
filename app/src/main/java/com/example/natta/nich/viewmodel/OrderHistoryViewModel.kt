package com.example.natta.nich.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.example.natta.nich.data.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OrderHistoryViewModel:ViewModel() {
    private var mRootRef = FirebaseDatabase.getInstance().reference
    private var mAuth = FirebaseAuth.getInstance()
    private var mOrder = MutableLiveData<ArrayList<Pair<String, Order>>>()

    fun getOrder(): MutableLiveData<ArrayList<Pair<String, Order>>> {
        val mOrderHistoryRef = mRootRef.child("order").orderByChild("finish").equalTo(true)
        mOrderHistoryRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                val orderList = arrayListOf<Pair<String, Order>>()
                p0.children.forEach {
                    val k = it.key!!
                    val v = it.getValue(Order::class.java)!!
                    if (v.customerID == mAuth.currentUser!!.uid)
                        orderList.add(Pair(k, v))
                }
                Log.d("getOrderHistory", p0.value.toString())
                mOrder.value = orderList
            }
        })
        return mOrder
    }
}