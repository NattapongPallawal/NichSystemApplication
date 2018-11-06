package com.example.natta.nich.view.food

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.chip.Chip
import android.support.design.chip.ChipGroup
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.airbnb.lottie.LottieAnimationView
import com.example.natta.nich.R
import com.example.natta.nich.data.Food
import com.example.natta.nich.view.myorder.MyOrderActivity
import com.example.natta.nich.viewmodel.FoodViewModel
import kotlinx.android.synthetic.main.activity_food.*
import java.util.*

class FoodActivity : AppCompatActivity(), FoodAdapter.OnItemClickListener, FoodViewModel.AddOrderListener {
    private var restaurant = ""
    private var type = arrayListOf<String>()
    private var food = arrayListOf<Pair<String, Food>>()
    private var selectType = ""
    private lateinit var adapter: FoodAdapter
    private var chipID: Int = 0
    private var model = FoodViewModel()
    private var selectFood = arrayListOf<Pair<String, Food>>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food)
        val actionBar = supportActionBar

        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.title = "เมนู"
        model = ViewModelProviders.of(this).get(FoodViewModel::class.java)
        model.setOnAddComplete(this)
        try {
            restaurant = intent.getStringExtra("resKey")
            model.setResKey(restaurant)
        } catch (e: IllegalStateException) {
            Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
        }

        adapter = FoodAdapter()
        adapter.setOnItemClickListener(this)
        setUpFood(adapter)


        model.getMenuType(restaurant).observe(this, Observer {
            if (it != null) {
                this.type = it
                addChip(it)
            }
        })

        menuType.setOnCheckedChangeListener { group, checkedId ->
            try {
                selectType = findViewById<Chip>(checkedId).chipText.toString()
                updateDataRecycleView()
                chipID = checkedId
            } catch (e: IllegalStateException) {
                findViewById<Chip>(chipID).isChecked = true
            }

        }

        model.getFood(restaurant)?.observe(this, Observer {
            if (it != null) {
                food = it
                updateDataRecycleView()
            }
        })

        shopping_cart.setOnClickListener {
            val i = Intent(applicationContext, MyOrderActivity::class.java)
            i.putExtra("resKey", restaurant)
            startActivity(i)
        }

    }

    private var addAni: LottieAnimationView? = null

    private var addToCart: ImageView? = null

    override fun onItemClick(position: Int, addAni: LottieAnimationView, addToCart: ImageView) {
        this.addAni = addAni
        this.addToCart = addToCart
        this.addToCart!!.visibility = View.GONE
        model.setSelectFood(selectFood)
        model.addOrderFood(position, addAni, addToCart)
        this.addAni!!.pauseAnimation()
        this.addAni!!.visibility = View.VISIBLE
        this.addAni!!.setAnimation("loading2.json")
        this.addAni!!.playAnimation()


    }

    override fun addComplete(food: String?, size: String, type: String?, price: Double?) {
        Snackbar.make(
            menu_layout, "เพิ่ม $food${type
                ?: ""} $size ราคา $price บาท ลงในออเดอร์ของคุณเรียบร้อยแล้ว", Snackbar.LENGTH_LONG
        ).show()
        this.addAni!!.pauseAnimation()
        this.addAni!!.setAnimation("check_animation.json")
        this.addAni!!.loop(false)
        AddToCart(this.addAni!!, this.addToCart!!).execute()
        this.addAni!!.playAnimation()

    }


    private fun updateDataRecycleView() {
        selectFood = arrayListOf()
        food.forEach {
            if (selectType == it.second.type) {
                selectFood.add(it)
            }
        }
        adapter.setData(selectFood, restaurant)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun setUpFood(adapter: FoodAdapter) {

        recyclerView_food.adapter = adapter
        recyclerView_food.layoutManager = GridLayoutManager(applicationContext, 2)
        recyclerView_food.setHasFixedSize(true)
        recyclerView_food.setItemViewCacheSize(10)
        recyclerView_food.isDrawingCacheEnabled = true
        recyclerView_food.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
    }


    @SuppressLint("PrivateResource")
    private fun addChip(type: ArrayList<String>) {
        try {
            val view = menuType as ChipGroup
            view.removeAllViews()
            for (i in 1..type.size) {

                val chip = Chip(view.context)
                chip.id = i + 1
                chip.chipText = type[i - 1]
                chip.setPadding(5, 5, 5, 5)
                chip.isClickable = true
                chip.isCheckable = true
                chip.gravity = Gravity.CENTER
                chip.chipBackgroundColor = resources.getColorStateList(R.color.mtrl_chip_background_color)
                chip.isFocusable = true
                chip.isCheckedIconEnabled = true
                view.addView(chip)
            }
            if (type.isNotEmpty()) {
                val l = findViewById<Chip>(1 + 1)
                l.isChecked = true
            }
        } catch (e: Exception) {
            Toast.makeText(applicationContext, "NO!!!!!", Toast.LENGTH_LONG).show()
        }

    }

    @SuppressLint("StaticFieldLeak")
    internal inner class AddToCart(private var addAni: LottieAnimationView, private var addToCart: ImageView) :
        AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg p0: Void?): Void? {

            Thread.sleep(3000)

            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            this.addAni.visibility = View.GONE
            this.addToCart.visibility = View.VISIBLE

        }
    }
}
