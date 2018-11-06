package com.example.natta.nich.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.os.AsyncTask
import com.example.natta.nich.data.Select
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MyOrderViewModel : ViewModel() {
    private var mRootRef = FirebaseDatabase.getInstance().reference
    private var mAuth = FirebaseAuth.getInstance()
    private var myOrder = MutableLiveData<ArrayList<Pair<String, Select>>>()
    private var resKey: String = ""

    private val listenerOrder = object : ValueEventListener {
        override fun onDataChange(p0: DataSnapshot) {
            val temp = arrayListOf<Pair<String, Select>>()
            p0.children.forEach {
                temp.add(Pair(it.key.toString(), it.getValue(Select::class.java)!!))
            }
            myOrder.value = temp
        }

        override fun onCancelled(p0: DatabaseError) {

        }
    }
    private lateinit var myOrderRef: Query

    fun getMyOrder(resID: String): MutableLiveData<ArrayList<Pair<String, Select>>> {
        val orderRef = mRootRef.child("temp/cart/${mAuth.currentUser!!.uid}/select").orderByChild("resID").equalTo(resID)
        myOrderRef = orderRef
        myOrderRef.addValueEventListener(listenerOrder)
        return myOrder
    }


    fun deleteSelectFoodAll(resID: String) {
        myOrder.value!!.forEach {
            val orderRef = mRootRef.child("temp/cart/${mAuth.currentUser!!.uid}/select/${it.first}")
            orderRef.removeValue()
        }
    }

    inner class DeleteSelectFoodAll(resID: String) : AsyncTask<Void, Void, Void>() {
        override fun onPreExecute() {
            super.onPreExecute()
            myOrderRef.removeEventListener(listenerOrder)
        }
        override fun doInBackground(vararg params: Void?): Void? {
            val a = myOrder.value!!
            a.forEach {
                val orderRef = mRootRef.child("temp/cart/${mAuth.currentUser!!.uid}/select/${it.first}")
                orderRef.setValue(null)
            }
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            myOrderRef.addValueEventListener(listenerOrder)
        }
    }
}