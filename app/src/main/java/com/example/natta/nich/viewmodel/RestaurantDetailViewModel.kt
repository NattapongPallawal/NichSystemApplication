package com.example.natta.nich.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.example.natta.nich.data.Customer
import com.example.natta.nich.data.FavRes
import com.example.natta.nich.data.Feedback
import com.example.natta.nich.data.Restaurant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class RestaurantDetailViewModel : ViewModel() {
    private var mRootRef = FirebaseDatabase.getInstance().reference
    private var mAuth = FirebaseAuth.getInstance()
    private var mFeedback = MutableLiveData<ArrayList<Feedback>>()
    private var mOneCustomer = MutableLiveData<Customer>()
    private val favResRef = mRootRef.child("favoriteRestaurant/${mAuth.currentUser!!.uid}")
    private var favResReady = MutableLiveData<Pair<Boolean, Boolean>>()
    private var ready = MutableLiveData<Boolean>()
    private var restaurant = Restaurant()
    private var resKey = ""

    init {
        ready.value = false
        favResReady.value = Pair(false, false)
        ready.observeForever { ready ->
            if (ready != null && ready) {
                favResReady.observeForever {
                    if (it != null && it.first && it.second) {
                        val favFood = FavRes(
                            resKey,
                            restaurant.restaurantName,
                            restaurant.picture,
                            ServerValue.TIMESTAMP,
                            restaurant.rating
                        )
                        val result = favResRef.push().setValue(favFood).isCanceled
                        if (result) {
                            favResReady.value = Pair(false, false)
                        }
                    }
                }
                val refQuery = favResRef.orderByChild("resID").equalTo(resKey).limitToFirst(1)
                refQuery.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.value == null) {
                            favResReady.value = Pair(true, false)
                        } else {
                            favResReady.value = Pair(false, false)
                        }

                    }

                    override fun onCancelled(p0: DatabaseError) {
                    }
                })
            }
        }
    }


    fun getFeedback(resID: String): MutableLiveData<ArrayList<Feedback>> {

        val mFeedbackRef = mRootRef.child("feedback").child(resID) // resID
        mFeedbackRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val feedbackList = arrayListOf<Feedback>()

                p0.children.forEach {
                    feedbackList.add(it.getValue(Feedback::class.java)!!)
                }
                var sumFeedback = 0.0
                feedbackList.forEach {
                    sumFeedback += it.rating!!
                }
                Log.d("getFeedback", "$sumFeedback : ${feedbackList.size} = ${sumFeedback / feedbackList.size}")
                try {
                    mRootRef.child("restaurant/$resID/rating").setValue(sumFeedback / feedbackList.size)
                } catch (e: DatabaseException) {

                }
                mFeedback.value = feedbackList
            }

        })
        return mFeedback

    }

    fun setFeedback(rating: Float, title: String, review: String, resKey: String) {
        val feedback = Feedback(mAuth.currentUser!!.uid, null, title, review, rating.toDouble(), ServerValue.TIMESTAMP)
        val mFeedbackRef = mRootRef.child("feedback").child(resKey)
        mFeedbackRef.push().setValue(feedback)
    }

    fun setData(resKey: String, restaurant: Restaurant) {
        this.resKey = resKey
        this.restaurant = restaurant
        ready.value = true
    }

    fun addFavoriteRes() {
        if (favResReady.value!!.first) {
            favResReady.value = Pair(true, true)
        }
    }
}