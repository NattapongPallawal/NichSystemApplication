package com.example.natta.nich.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ScanQRCodeViewModel : ViewModel() {
    var ready = MutableLiveData<Boolean>()
    private var mRootRef = FirebaseDatabase.getInstance().reference
    var tableName = ""
    var restaurantName = ""
    var tableStatus = true
    private var mAuth = FirebaseAuth.getInstance()

    fun getRestaurantName(resID : String,tableID : String) {
        mRootRef.child("restaurant/$resID/restaurantName")
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    restaurantName = p0.value.toString()
                    getTableName(resID,tableID)
                }

            })
    }

    private fun getTableName(resID: String, tableID: String) {
        mRootRef.child("table/$resID/$tableID")
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    val tempCusId = p0.child("customerID").value.toString()
                    tableName = p0.child("tableName").value.toString()
                    tableStatus = if (tempCusId != "null"){
                        p0.child("available").value.toString().toBoolean() || tempCusId == mAuth.currentUser!!.uid
                    }else{
                        p0.child("available").value.toString().toBoolean()

                    }
                    ready.value = true
                }

            })
    }
}