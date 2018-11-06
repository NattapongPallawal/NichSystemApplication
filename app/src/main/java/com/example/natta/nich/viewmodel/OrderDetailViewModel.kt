package com.example.natta.nich.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.example.natta.nich.data.Order
import com.example.natta.nich.data.OrderMenu
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OrderDetailViewModel : ViewModel() {
    private var mRootRef = FirebaseDatabase.getInstance().reference
    private var mAuth = FirebaseAuth.getInstance()
    private var mOrder = MutableLiveData<Order>()
    private var mOrderMenu = MutableLiveData<ArrayList<Pair<String, OrderMenu>>>()
    private var key = ""

    init {
        mOrder.observeForever {
            if (it != null) {
                getOrderMenu(key)
            }
        }
    }

    fun getMenu() = mOrderMenu


    fun getOrder(key: String): MutableLiveData<Order> {
        this.key = key
        val ref = mRootRef.child("order/$key")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val orderTemp = p0.getValue(Order::class.java)!!
                mOrder.value = orderTemp

            }

        })
        return mOrder
    }

    private fun getOrderMenu(key: String) {
        val ref = mRootRef.child("order-menu/$key")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val orderMenuTemp = arrayListOf<Pair<String, OrderMenu>>()
                p0.children.forEach {
                    val k = it.key!!
                    val orderMenu = it.getValue(OrderMenu::class.java)!!
                    orderMenuTemp.add(Pair(k, orderMenu))
                }
                mOrderMenu.value = orderMenuTemp
//                Log.d("getOrderMenu", mOrderMenu.value!!.first().second.price.toString())


            }

            override fun onCancelled(p0: DatabaseError) {
            }

        })

    }
}