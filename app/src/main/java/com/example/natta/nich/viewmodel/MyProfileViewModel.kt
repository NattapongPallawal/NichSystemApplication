package com.example.natta.nich.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.example.natta.nich.data.Customer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MyProfileViewModel : ViewModel() {
    private var mRootRef = FirebaseDatabase.getInstance().reference
    private var mAuth = FirebaseAuth.getInstance()
    private var mCustomer = MutableLiveData<Customer>()
    private var mEmail = " "
    private var customer = Customer()
    private var uid = mAuth.currentUser!!.uid
    private val mCustomerRef = mRootRef.child("customer").child(mAuth.currentUser!!.uid)


    fun getCustomer(): MutableLiveData<Customer> {
        mCustomerRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                mCustomer.value = p0.getValue(Customer::class.java)
            }
        })
        return mCustomer
    }

    fun getEmail(): String {
        mEmail = mAuth.currentUser?.email.toString()
        return mEmail
    }

    fun setCustomer(customer: Customer) {
        mCustomerRef.setValue(customer)
            .addOnFailureListener {
                throw Exception(it.message)
            }
            .addOnSuccessListener {

            }

    }

    fun getUID(): String {
        return uid
    }
}