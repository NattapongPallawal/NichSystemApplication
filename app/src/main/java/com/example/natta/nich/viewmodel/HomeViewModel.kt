package com.example.natta.nich.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel;
import android.util.Log
import com.example.natta.nich.data.Order
import com.example.natta.nich.data.OrderMenu
import com.example.natta.nich.data.Restaurant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeViewModel : ViewModel() {
    private var mRootRef = FirebaseDatabase.getInstance().reference
    private var mAuth = FirebaseAuth.getInstance()
    private var mOrder = MutableLiveData<Pair<String, Order>>()
    private var mMenu = MutableLiveData<Pair<String, OrderMenu>>()
    private var mRestaurant = MutableLiveData<ArrayList<Pair<String, Restaurant>>>()
    private var mOrderKey = ""
    var ready = MutableLiveData<Pair<Boolean,Boolean>>()


    init {
        ready.value = Pair(false,false)

        getLastOrder()
        ready.observeForever { ready ->
            if (ready != null) {
                if (ready.first) {
                    getLastMenu()
                }
            }
        }
        getRestaurant()
    }

    private fun getLastMenu() {
        val ref = mRootRef.child("order-menu/$mOrderKey").orderByKey().limitToFirst(1)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("getLastMenuE", p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                try {
                    val k = p0.key
                    val v = p0.children.first().getValue(OrderMenu::class.java)
                    if (v != null && k != null) {
                        Log.d("getLastMenu", "${p0.key} ${v.picture}")
                        mMenu.value = Pair(k, v)
                    }
                }catch (e:Exception){}



            }

        })
    }

    private fun getLastOrder() {
        val ref = mRootRef.child("order")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
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
                        Log.d("checkOrder", order.paymentType)

                    }
                    orderTemp.sortWith(Comparator { o1, o2 ->
                        val a = o1!!.second.date as Long
                        val b = o2!!.second.date as Long
                        when {
                            a < b -> 1
                            a == b -> 0
                            else -> -1
                        }
                    })

                    if (orderTemp.isNotEmpty()) {
                        mOrder.value = orderTemp.first()
                        mOrderKey = orderTemp.first().first
                        ready.value = Pair(true, !ready.value!!.second)
                    } else {
                        ready.value = Pair(false,ready.value!!.second)
                    }
                }catch (e:Exception){}

            }

        })


    }

    fun getOrder(): MutableLiveData<Pair<String, Order>> {
        return mOrder
    }

    fun getMenu(): MutableLiveData<Pair<String, OrderMenu>> {
        return mMenu
    }

    fun getNearbyRes(): MutableLiveData<ArrayList<Pair<String, Restaurant>>> {
        return mRestaurant
    }

    fun getPopularRes(): MutableLiveData<ArrayList<Pair<String, Restaurant>>> {
        return mRestaurant
    }

    private fun getRestaurant(): MutableLiveData<ArrayList<Pair<String, Restaurant>>> {
        val ref = mRootRef.child("restaurant")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val restaurants = arrayListOf<Pair<String, Restaurant>>()
                p0.children.forEach{ restaurant ->
                    val k = restaurant.key!!
                    val v = restaurant.getValue(Restaurant::class.java)!!
                    restaurants.add(Pair(k,v))
                }
                mRestaurant.value = restaurants
            }
        })
        return mRestaurant
    }

}
