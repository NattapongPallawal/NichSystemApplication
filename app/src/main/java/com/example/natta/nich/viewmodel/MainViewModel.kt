package com.example.natta.nich.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.example.natta.nich.data.Customer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.lang.Exception

class MainViewModel : ViewModel() {
    private var mRootRef = FirebaseDatabase.getInstance().reference
    private var mAuth = FirebaseAuth.getInstance()
    private lateinit var mCustomerRef: DatabaseReference
    private var mCustomerLiveData = MutableLiveData<Customer>()
    private var mEmail: String? = null


    fun getCustomer(): MutableLiveData<Customer> {
        return try {
            mCustomerRef = mRootRef.child("customer").child(mAuth.currentUser!!.uid)
            mCustomerRef.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}
                override fun onDataChange(p0: DataSnapshot) {
                    mCustomerLiveData.value = p0.getValue(Customer::class.java)
                }
            })
            mCustomerLiveData
        } catch (e: Exception) {
            mCustomerLiveData
        }

    }

    fun getEmail(): String {
        mEmail = mAuth.currentUser?.email.toString()
        return mEmail as String
    }

}