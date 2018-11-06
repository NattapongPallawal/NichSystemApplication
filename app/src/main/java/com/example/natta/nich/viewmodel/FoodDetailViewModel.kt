package com.example.natta.nich.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.example.natta.nich.data.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FoodDetailViewModel : ViewModel() {
    private var mRootRef = FirebaseDatabase.getInstance().reference
    private var mAuth = FirebaseAuth.getInstance()
    private var food = Food()
    private var amount: Int = 1
    private var ready = MutableLiveData<Boolean>()
    private var mFoodSize = MutableLiveData<ArrayList<Pair<String, FoodSize>>>()
    private var mFoodType = MutableLiveData<ArrayList<Pair<String, FoodType>>>()
    private var resKey: String = ""
    private var foodKey: String = ""
    private var positionFoodType: Int = 0
    private var positionFoodSize: Int = 0
    private val favFoodRef = mRootRef.child("favoriteFood/${mAuth.currentUser!!.uid}")
    private var favFoodReady = MutableLiveData<Pair<Boolean, Boolean>>()
    private var resName = ""

    init {
        ready.value = false
        favFoodReady.value = Pair(false, false)
        ready.observeForever { ready ->
            if (ready != null && ready) {

                favFoodReady.observeForever {
                    if (it != null && it.first && it.second) {
                        val favFood = FavFood(
                            foodKey,
                            food.foodName,
                            food.picture,
                            ServerValue.TIMESTAMP,
                            resKey,
                            resName,
                            food.rate
                        )
                        val result = favFoodRef.push().setValue(favFood).isCanceled
                        if (result) {
                            favFoodReady.value = Pair(false, false)
                        }
                    }
                }
                val refQuery = favFoodRef.orderByChild("foodID").equalTo(foodKey).limitToFirst(1)
                refQuery.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.value == null) {
                            favFoodReady.value = Pair(true, false)
                            Log.d("checkFav", "${p0.value}")
                        } else {
                            favFoodReady.value = Pair(false, false)
                            Log.d("checkFavH", "${p0.value}")
                        }

                    }

                    override fun onCancelled(p0: DatabaseError) {
                        Log.d("checkFavE", p0.message)

                    }
                })
            }
        }


    }


    fun addOrderFood(t: Double = 0.0, formFood: Boolean, selectKey: String) {
        var total = t
        total = try {
            t + mFoodType.value!![positionFoodType].second.price!! +
                    mFoodSize.value!![positionFoodSize].second.price!!
        } catch (e: Exception) {
            t + mFoodSize.value!![positionFoodSize].second.price!!
        }

        if (formFood) {
            val foodRef = mRootRef.child("temp/cart/${mAuth.currentUser!!.uid}/select")
            when {
                mFoodType.value!!.isNotEmpty() -> {
                    val food = Select(
                        amount,
                        foodKey,
                        mFoodSize.value!![positionFoodSize].first,
                        mFoodType.value!![positionFoodType].first,
                        resKey,
                        food.foodName,
                        mFoodType.value!![positionFoodType].second.ingredientName,
                        mFoodSize.value!![positionFoodSize].second.size,
                        total,
                        food.picture
                    )

                    foodRef.push().setValue(food)

                }
                else -> {
                    val food = Select(
                        amount,
                        foodKey,
                        mFoodSize.value!![positionFoodSize].first,
                        null,
                        resKey,
                        food.foodName,
                        null,
                        mFoodSize.value!![positionFoodSize].second.size,
                        total,
                        food.picture
                    )

                    foodRef.push().setValue(food)
                }
            }
        } else {
            val foodRef = mRootRef.child("temp/cart/${mAuth.currentUser!!.uid}/select/$selectKey")
            when {
                mFoodType.value!!.isNotEmpty() -> {
                    val food = Select(
                        amount,
                        foodKey,
                        mFoodSize.value!![positionFoodSize].first,
                        mFoodType.value!![positionFoodType].first,
                        resKey,
                        food.foodName,
                        mFoodType.value!![positionFoodType].second.ingredientName,
                        mFoodSize.value!![positionFoodSize].second.size,
                        total,
                        food.picture
                    )

                    foodRef.setValue(food)

                }
                else -> {
                    val food = Select(
                        amount,
                        foodKey,
                        mFoodSize.value!![positionFoodSize].first,
                        null,
                        resKey,
                        food.foodName,
                        null,
                        mFoodSize.value!![positionFoodSize].second.size,
                        total,
                        food.picture
                    )

                    foodRef.setValue(food)
                }
            }
        }


    }

    fun getFoodTypeSize(): String {
        var str = ""
        try {
            str = mFoodType.value!![positionFoodType].second.ingredientName + " " +
                    mFoodSize.value!![positionFoodSize].second.size
        } catch (e: Exception) {
            str = mFoodSize.value!![positionFoodSize].second.size!!
        } catch (e: Exception) {
            Log.d("getFoodTypeSize", e.message)
        }
        return str
    }

    fun getAmount(): Int {
        return amount
    }

    fun getFood(): Food {
        return food
    }

    fun getReady(): MutableLiveData<Boolean> {
        return ready
    }

    fun setResFoodKey(resKey: String, foodKey: String, food: Food?, amount: Int = 1) {
        this.resKey = resKey
        this.foodKey = foodKey
        this.amount = amount
        if (food != null) {
            this.food = food
            getNameRes()
        } else {
            getOneFood()
            getNameRes()
        }


    }

    private fun getNameRes() {
        val ref = mRootRef.child("restaurant/$resKey/restaurantName")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                Log.d("resName", "${p0.value}")
                if (p0.value != null) {
                    resName = p0.value.toString()
                    ready.value = true
                }


            }

        })
    }

    private fun getOneFood() {
        val foodRef = mRootRef.child("menu/$resKey/$foodKey")
        foodRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                food = p0.getValue(Food::class.java)!!
                ready.value = true
            }

        })
    }


    fun setPositionFoodSize(positionFoodSize: Int) {
        this.positionFoodSize = positionFoodSize
    }

    fun setPositionFoodType(positionFoodType: Int) {
        this.positionFoodType = positionFoodType
    }

    fun getPositionFoodSize(): Int {
        return this.positionFoodSize
    }

    fun getPositionFoodType(): Int {
        return this.positionFoodType
    }

    fun addAmount(): String {
        if (checkAmount()) {
            if (amount != 99)
                amount = ++amount
        }
        return amount.toString()
    }

    fun removeAmount(): String {
        if (checkAmount()) {
            if (amount != 1)
                amount = --amount
        }
        return amount.toString()
    }

    private fun checkAmount(): Boolean {
        if (amount in 1..99) {
            return true
        }
        return false
    }

    fun getFoodSize(): MutableLiveData<ArrayList<Pair<String, FoodSize>>> {
        val fSize = mRootRef.child("foodSize").child(this.resKey).child(this.foodKey)
        Log.d("foodSize", "res : $resKey , food : $foodKey")
        fSize.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val foodSize = arrayListOf<Pair<String, FoodSize>>()
                p0.children.forEach {
                    val v = it.getValue(FoodSize::class.java)!!
                    foodSize.add(Pair(it.key.toString(), v))
                    Log.d("foodSize", it.child("size").getValue(String::class.java))
                }
                foodSize.forEach {
                    Log.d("foodSize", it.first)
                }
                mFoodSize.value = foodSize
            }
        })

        return mFoodSize
    }

    fun getFoodType(): MutableLiveData<ArrayList<Pair<String, FoodType>>> {
        val type = mRootRef.child("foodType").child(resKey).child(foodKey).orderByChild("available").equalTo(true)
        type.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val foodType = arrayListOf<Pair<String, FoodType>>()
                p0.children.forEach {
                    foodType.add(Pair(it.key.toString(), it.getValue(FoodType::class.java)!!))
                }
                mFoodType.value = foodType

            }

        })

        return mFoodType
    }

    fun addFavoriteFood() {
        if (favFoodReady.value!!.first) {
            favFoodReady.value = Pair(true, true)
        }


    }
}