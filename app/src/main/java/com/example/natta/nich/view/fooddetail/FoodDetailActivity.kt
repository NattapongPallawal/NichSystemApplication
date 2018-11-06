package com.example.natta.nich.view.fooddetail

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.chip.Chip
import android.support.design.chip.ChipGroup
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.natta.nich.R
import com.example.natta.nich.data.Food
import com.example.natta.nich.data.FoodSize
import com.example.natta.nich.data.FoodType
import com.example.natta.nich.data.Select
import com.example.natta.nich.viewmodel.FoodDetailViewModel
import kotlinx.android.synthetic.main.activity_food_detail.*

class FoodDetailActivity : AppCompatActivity() {
    private var model: FoodDetailViewModel? = null
    private var food = Food()
    private var key = ""
    private var selectKey = ""
    private var resKey: String = ""
    private var select = Select()
    private var formFood: Boolean = true
    private var chipIDType: Int = 0
    private var chipIDSize: Int = 0
    private var total: Double = 0.0
    private var mFoodSize = arrayListOf<Pair<String, FoodSize>>()
    private var mFoodType = arrayListOf<Pair<String, FoodType>>()
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_detail)
        setSupportActionBar(toolbar_FD)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = "รายละเอียดเมนู"
        }
        actionBar!!.setDisplayHomeAsUpEnabled(true)

        model = ViewModelProviders.of(this).get(FoodDetailViewModel::class.java)
        key = intent.getStringExtra("foodKey")
        resKey = intent.getStringExtra("resKey")
        try {
            food = intent.getParcelableExtra("food")
            model!!.setResFoodKey(resKey, key, food)
            formFood = true
        } catch (e: IllegalStateException) {
            select = intent.getParcelableExtra("select")
            selectKey = intent.getStringExtra("selectKey")
            model!!.setResFoodKey(resKey, key, null, select.amount!!)
            formFood = false

        }

        model!!.getReady().observe(this, Observer {
            if (it!!) {
                food = model!!.getFood()
                initView()
                updatePrice()
            }
        })

        model!!.getFoodSize().observe(this, Observer { list ->
            if (list != null) {
                val temp = arrayListOf<Pair<String, String>>()
                list.forEach {
                    temp.add(Pair(it.first, it.second.size!!))
                }
                mFoodSize = list
                addChip(foodSize, temp)
                updatePrice()
            }
        })

        model!!.getFoodType().observe(this, Observer { list ->
            if (list != null) {
                val temp = arrayListOf<Pair<String, String>>()
                list.forEach {
                    temp.add(Pair(it.first, it.second.ingredientName!!))
                }

                addChip(foodType, temp)
                mFoodType = list
                if (list.isNotEmpty()) {
                    showFoodType()
                } else {
                    hideFoodType()
                }
                updatePrice()
            }
        })

        foodType.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId != -1) {
                try {
                    var t = findViewById<Chip>(checkedId)
                    model!!.setPositionFoodType(checkedId - 2)
                    chipIDType = checkedId

                    Log.d("checkedId1", "$checkedId")

                } catch (e: Exception) {
                    findViewById<Chip>(chipIDType).isChecked = true
                }
            } else {
                findViewById<Chip>(chipIDType).isChecked = true
            }
            updatePrice()
        }
        foodSize.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId != -1) {
                try {
                    var t = findViewById<Chip>(checkedId)
                    model!!.setPositionFoodSize(checkedId / 100 - 1)
                    chipIDSize = checkedId

                    Log.d("checkedId2", "$checkedId")
                } catch (e: Exception) {
                    findViewById<Chip>(chipIDSize).isChecked = true

                }
            } else {
                Log.d("checkedId3", "$chipIDSize")
                findViewById<Chip>(chipIDSize).isChecked = true
            }
            updatePrice()
        }


    }

    private fun updatePrice() {
        try {
            total = food.price!! + mFoodType[model!!.getPositionFoodType()].second.price!! +
                    mFoodSize[model!!.getPositionFoodSize()].second.price!!
        } catch (e: IndexOutOfBoundsException) {

        }
        price_FD.text = total.toString() + " ฿"
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun showFoodType() {
        textView_mat_FD.visibility = View.VISIBLE
        foodType.visibility = View.VISIBLE
        div_mat_FD.visibility = View.VISIBLE
    }

    private fun hideFoodType() {
        textView_mat_FD.visibility = View.GONE
        foodType.visibility = View.GONE
        div_mat_FD.visibility = View.GONE
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        Glide.with(this).load(food.picture).apply(RequestOptions.errorOf(R.drawable.ic_error)).into(pic_FD)
        total += food.price!!
        foodName_FD.text = food.foodName
        rating_FD.rating = food.rate!!.toFloat()
        price_FD.text = total.toString() + " ฿"

        amount_FD.text = model!!.getAmount().toString()

        removeAmount_FD.setOnClickListener {
            amount_FD.text = model!!.removeAmount()
        }

        addAmount_FD.setOnClickListener {
            amount_FD.text = model!!.addAmount()
        }
        btn_confirm.setOnClickListener {
            try {
                model!!.addOrderFood(food.price!!, formFood, selectKey)
                Toast.makeText(
                    applicationContext,
                    "${if (formFood) "เพิ่ม" else "แก้ไข"} ${food.foodName} ${model!!.getFoodTypeSize()} " +
                            "\nจำนวน ${model!!.getAmount()} รายการ เรียบร้อยแล้ว",
                    Toast.LENGTH_LONG
                )
                    .show()
                finish()
            } catch (e: IndexOutOfBoundsException) {
                // Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_LONG).show()
                Log.d("getFoodTypeSize",e.message)
            }

        }

        btn_cancel.setOnClickListener {
            finish()
        }
        fab_add_food_fav.setOnClickListener {
            model!!.addFavoriteFood()
            Snackbar.make(food_detail, "เพิ่ม ${food.foodName} \nลงในรายการโปรเรียบร้อยแล้ว", Snackbar.LENGTH_LONG)
                .show()
        }
    }

    @SuppressLint("PrivateResource")
    private fun addChip(view: ChipGroup, data: ArrayList<Pair<String, String>>?) {
        view.removeAllViews()
        var change = true
        var chipID = 0
        if (data != null) {
            for (i in 1..data.size) {
                Log.d("addChip", i.toString())
                val chip = Chip(view.context)
                when (view.id) {
                    R.id.foodType -> {
                        chip.id = i + 1
                        chip.chipText = data[i - 1].second
                        if (data[i - 1].first == select.foodTypeID) {
                            chipID = i + 1
                            change = false
                        }
                        if (change) {
                            chipID = 2
                        }
                    }
                    R.id.foodSize -> {
                        chip.id = i * 100 + 1
                        chip.chipText = data[i - 1].second
                        if (data[i - 1].first == select.foodSizeID) {
                            chipID = i * 100 + 1
                            change = false
                        }
                        if (change) {
                            chipID = 1 * 100 + 1
                        }
                    }
                }
                chip.setPadding(5, 5, 5, 5)
                chip.isClickable = true
                chip.isCheckable = true
                chip.gravity = Gravity.CENTER
                chip.chipBackgroundColor = resources.getColorStateList(R.color.mtrl_chip_background_color)
                chip.isFocusable = true
                view.addView(chip)
            }
        }
        if (data != null) {
            if (data.isNotEmpty()) {
                if (view.id == R.id.foodSize) {
                    val c = findViewById<Chip>(chipID)
                    c.isChecked = true
                } else {
                    val c = findViewById<Chip>(chipID)
                    c.isChecked = true
                }
            } else {

            }
        }
    }
}
