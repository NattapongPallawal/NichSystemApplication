package com.example.natta.nich.viewmodel

import android.annotation.SuppressLint
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.example.natta.nich.data.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class PaymentViewModel : ViewModel() {
    private var mRootRef = FirebaseDatabase.getInstance().reference
    private var mAuth = FirebaseAuth.getInstance()
    private var myOrder = MutableLiveData<ArrayList<Pair<String, Select>>>()
    private var restaurant = MutableLiveData<Restaurant>()
    private var orderNumber = MutableLiveData<Int>()
    private var total: Double = 0.0
    private var queue: Int = 0
    private var restaurantID: String = ""
    private var fromRestaurant: Int = -1
    private var tableKey: String? = null
    private var tableName = MutableLiveData<String>()

    init {
        orderNumber.value = -1
    }

    fun getTable(): MutableLiveData<String> {
        return tableName
    }

    fun getTotal(): Double {
        return total
    }

    fun setFromRestaurant(fromRestaurant: Int) {
        this.fromRestaurant = fromRestaurant
    }

    @SuppressLint("SimpleDateFormat")
    fun addOrder(selectPromtPay: Boolean) {

        if (fromRestaurant != -1) {
            if (fromRestaurant == 1) {
                Log.d("addOrder",fromRestaurant.toString())
                mRootRef.child("table/$restaurantID/${this.tableKey}/available").setValue(false)
                mRootRef.child("table/$restaurantID/${this.tableKey}/customerID").setValue(mAuth.currentUser!!.uid)
            }
            val resRef = mRootRef.child("order")
            val orderNumberRef = mRootRef.child("temp/orderNumber/$restaurantID")
            resRef.push().setValue(
                Order(
                    queue + 1,
                    total,
                    if (fromRestaurant == 1) {
                        "รอรับออเดอร์"
                    } else {
                        "รอการชำระเงิน"
                    },
                    ServerValue.TIMESTAMP,
                    restaurantID,
                    if (selectPromtPay) {
                        "promtpay"
                    } else {
                        "cash"
                    },
                    restaurant.value!!.restaurantName,
                    0,
                    if (fromRestaurant == 1) {
                        4
                    } else {
                        4
                    },
                    fromRestaurant == 1,
                    false,
                    myOrder.value!!.size,
                    mAuth.currentUser!!.uid,
                    tableKey
                )
            )
            { p0, p1 ->
                val o = arrayListOf<OrderMenu>()
                val orderAmount = arrayListOf<Pair<String, Int>>()
                myOrder.value!!.forEach {
                    if (checkAmount(orderAmount, it.second.foodID) != -1) {
                        val tempOrderAmount =
                            Pair(it.second.foodID!!, 1 + orderAmount[checkAmount(orderAmount, it.second.foodID)].second)
                        orderAmount[checkAmount(orderAmount, it.second.foodID)] = tempOrderAmount
                    } else {
                        val tempOrderAmount = Pair(it.second.foodID!!, 1)
                        orderAmount.add(tempOrderAmount)
                    }
                    val temp = OrderMenu(
                        it.second.foodSizeID,
                        it.second.foodTypeID,
                        it.second.amount,
                        it.second.foodID,
                        it.second.price,
                        it.second.foodName,
                        it.second.foodTypeName,
                        it.second.foodSizeName,
                        it.second.finish,
                        it.second.picture
                    )
                    o.add(temp)
                }
                mRootRef.child("order-menu/${p1.key}").setValue(o)
                { _, _ ->
                    orderAmount.forEach {
                        val f = mRootRef.child("menu/$restaurantID/${it.first}")
                        f.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                val oa: Int = p0.child("orderAmount").value.toString().toInt() + it.second
                                mRootRef.child("menu/$restaurantID/${it.first}/orderAmount").setValue(oa)
                                calculateRating()
                            }
                        })
                    }
                    val updateQueue = hashMapOf("queue" to queue + 1, "date" to ServerValue.TIMESTAMP)
                    orderNumberRef.setValue(updateQueue)
                    deleteSelectFoodAll()

                }



                sendNotification(p1.key!!)
            }
        }
    }

    private fun checkAmount(orderAmount: ArrayList<Pair<String, Int>>, foodID: String?): Int {
        orderAmount.forEach {
            if (it.first == foodID) {
                return orderAmount.indexOf(it)
            }
        }
        return -1
    }

    private fun sendNotification(orderKey: String) {
        val fromRes = fromRestaurant == 1
        val notificationRestaurant = NotificationRestaurant(
            mAuth.currentUser!!.uid,
            ServerValue.TIMESTAMP,
            "มีออเดอร์ใหม่ ${myOrder.value!!.size} รายการ",
            orderKey,
            restaurantID,
            fromRes
        )
        mRootRef.child("notificationRestaurant").push().setValue(notificationRestaurant)
    }

    @SuppressLint("SimpleDateFormat")
    private fun getOrderNumber() {
        val orderNumberRef = mRootRef.child("temp/orderNumber/$restaurantID")
        val resetQueue = hashMapOf("queue" to 0, "date" to ServerValue.TIMESTAMP)
        val today = SimpleDateFormat("ddMMyyyy").format(Date()).toLong()
        Log.d("orderNumberDateToday", today.toString())
        orderNumberRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                try {
                    val queueTemp = p0.child("queue").getValue(Int::class.java)!!
                    val timeStamp: Long = p0.child("date").getValue(Long::class.java)!!
                    val dateOrder = SimpleDateFormat("ddMMyyyy").format(Date(timeStamp)).toLong()

                    Log.d("orderNumber", "queue = $queue , queueTemp = $queueTemp date = $timeStamp")
                    Log.d("orderNumber2date", "$dateOrder == $today : timeStamp = $timeStamp")
                    if (today == dateOrder) {
                        queue = queueTemp
                        Log.d("orderNumberQueue", queue.toString())
                    } else {
                        orderNumberRef.setValue(resetQueue)
                    }

                } catch (e: KotlinNullPointerException) {
                    orderNumberRef.setValue(resetQueue)
                }
            }
        })
    }

    fun getMyOrder(resID: String): MutableLiveData<ArrayList<Pair<String, Select>>> {
        val orderRef =
            mRootRef.child("temp/cart/${mAuth.currentUser!!.uid}/select").orderByChild("resID").equalTo(resID)
        orderRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val temp = arrayListOf<Pair<String, Select>>()
                total = 0.0
                p0.children.forEach {
                    temp.add(Pair(it.key.toString(), it.getValue(Select::class.java)!!))
                    val p = it.child("price").getValue(Double::class.java)!!
                    val a = it.child("amount").getValue(Int::class.java)!!
                    total += p * a
                }
                myOrder.value = temp
            }

            override fun onCancelled(p0: DatabaseError) {

            }


        })
        return myOrder
    }

    fun getRestaurant(resID: String): MutableLiveData<Restaurant> {
        this.restaurantID = resID
        getOrderNumber()
        val resRef = mRootRef.child("restaurant").orderByKey().equalTo(resID)

        resRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val res = p0.child(resID).getValue(Restaurant::class.java)
                restaurant.value = res
            }

        })

        return restaurant
    }

    private fun deleteSelectFoodAll() {
        myOrder.value!!.forEach {
            val orderRef = mRootRef.child("temp/cart/${mAuth.currentUser!!.uid}/select/${it.first}")
            orderRef.removeValue()
        }
    }

    fun setTable(tableKey: String?, resKey: String) {
        this.tableKey = tableKey
        mRootRef.child("table/$resKey/${this.tableKey}/tableName")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    tableName.value = p0.value.toString()
                    Log.d("setTable", p0.value.toString())
                }

            })

    }

    private fun calculateRating() {
        val menus = arrayListOf<Pair<String, Food>>()
        val dataRef = mRootRef.child("menu/$restaurantID")
        dataRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    val temp = Pair(it.key!!, it.getValue(Food::class.java)!!)
                    menus.add(temp)
                }
                val max = findMax(menus)

                menus.forEach {
                    val rating = 5 * it.second.orderAmount!! / max
                    mRootRef.child("menu/$restaurantID/${it.first}/rate").setValue(rating)

                }
                Log.d("calculateRating", menus.size.toString())
            }

        })
    }

    private fun findMax(menus: ArrayList<Pair<String, Food>>): Int {
        var max = 1
        menus.forEach {
            if (it.second.orderAmount != null) {
                if (it.second.orderAmount!! > max) {
                    max = it.second.orderAmount!!
                }
            }

        }
        return max
    }

}