package com.example.natta.nich.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel;
import android.util.Log
import com.example.natta.nich.data.Notification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class NotificationViewModel : ViewModel() {
    private var mNoti = MutableLiveData<List<Notification>>()
    private var mRootRef = FirebaseDatabase.getInstance().reference
    private var mAuth = FirebaseAuth.getInstance()

    fun getNotification(): MutableLiveData<List<Notification>> {
        val mNotiRef = mRootRef.child("notificationCustomer").orderByChild("customer").equalTo(mAuth.currentUser!!.uid) // uid
        mNotiRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }
            override fun onDataChange(p0: DataSnapshot) {
                val noti = arrayListOf<Notification>()
                var notification: Notification
                p0.children.forEach {
                    val resKey = it.getValue(Notification::class.java)!!.restaurant
                    notification = it.getValue(Notification::class.java)!!
                    noti.add(notification)
                    Log.d("resKeyNOti",it.value.toString())
                    mRootRef.child("/restaurant/$resKey/restaurantName")
                        .addValueEventListener(object  : ValueEventListener{
                            override fun onCancelled(p0: DatabaseError) {

                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                Log.d("resKeyNOti",p0.value.toString())
                            }

                        })

                }
                mNoti.value = noti
            }
        })
        return mNoti
    }
}
