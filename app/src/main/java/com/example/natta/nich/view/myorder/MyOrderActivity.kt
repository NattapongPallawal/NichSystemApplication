package com.example.natta.nich.view.myorder

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Menu
import android.view.MenuItem
import com.example.natta.nich.R
import com.example.natta.nich.data.Select
import com.example.natta.nich.view.payment.PaymentActivity
import com.example.natta.nich.viewmodel.MyOrderViewModel
import kotlinx.android.synthetic.main.activity_my_order.*

class MyOrderActivity : AppCompatActivity() {
    private var model = MyOrderViewModel()
    private var resKey: String = ""
    private var myOrder = arrayListOf<Pair<String, Select>>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_order)
        resKey = intent.getStringExtra("resKey")
        model = ViewModelProviders.of(this).get(MyOrderViewModel::class.java)

        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.title = "ออเดอร์ของฉัน"
        val adapter = MyOrderAdapter()
        recyclerView_MO.adapter = adapter
        recyclerView_MO.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView_MO.setHasFixedSize(true)
        recyclerView_MO.setItemViewCacheSize(20)
        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = recyclerView_MO.adapter as MyOrderAdapter
                adapter.removeAt(viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recyclerView_MO)



        model.getMyOrder(resKey).observe(this, Observer {
            if (it != null) {
                myOrder = it
                adapter.setData(it)
                item_food_MO.text = "จำนวน ${it.size} รายการ"

            }
        })

        adapter.getTotal().observe(this, Observer {
            total_MO.text = "ยอดชำระ $it ฿"
        })

        btn_payment_MO.setOnClickListener {
            if (myOrder.size != 0){
                val i = Intent(applicationContext, PaymentActivity::class.java)
                i.putExtra("resKey",resKey)
                startActivity(i)
            }else{
                Snackbar.make(my_order_layout, "กรุณาสั่งอาหาร", Snackbar.LENGTH_LONG).show()
            }

        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_my_oeder, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null) {
            when (item.itemId) {
                R.id.deleteFoodAll -> {
                    if (myOrder.isNotEmpty()) {
                        val builder = AlertDialog.Builder(this@MyOrderActivity)
                        builder.setTitle("ลบทั้งหมด")
                        builder.setMessage("คุณต้องการที่จะลบรายการอาหารที่คุณเลือกทั้งหมดหรือไม่?")
                        builder.setPositiveButton("ใช่") { _, _ ->
                            model.DeleteSelectFoodAll(resKey).execute()
                            Snackbar.make(my_order_layout, "ลบรายการอาหารที่คุณเลือกทั้งหมดเรียบร้อยแล้ว", Snackbar.LENGTH_LONG).show()
                        }
                        builder.setNegativeButton("ไม่ใช่") { dialog, _ -> dialog.dismiss() }

                        val dialog = builder.create()
                        dialog.show()
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
                    }else{
                        Snackbar.make(my_order_layout, "กรุณาสั่งอาหาร", Snackbar.LENGTH_LONG).show()
                    }

                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
