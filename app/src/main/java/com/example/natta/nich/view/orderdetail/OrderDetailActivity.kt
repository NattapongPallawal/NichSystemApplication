package com.example.natta.nich.view.orderdetail

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import com.example.natta.nich.R
import com.example.natta.nich.viewmodel.OrderDetailViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rakshakhegde.stepperindicator.StepperIndicator
import kotlinx.android.synthetic.main.activity_order_detail.*
import java.text.SimpleDateFormat
import java.util.*


class OrderDetailActivity : AppCompatActivity() {
    private var orderKey: String = ""
    private lateinit var model: OrderDetailViewModel
    private var mRootRef = FirebaseDatabase.getInstance().reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.title = "รายละเอียดออเดอร์"
        model = ViewModelProviders.of(this).get(OrderDetailViewModel::class.java)
        orderKey = intent.getStringExtra("orderKey")
        model.getOrder(orderKey).observe(this, Observer {
            if (it != null) {
                stepperIndicator.stepCount = it.numStatus!!
                stepperIndicator.currentStep = it.currentStatus!!
                status_OD.text = it.status.toString()
                orderNum_OD.text = "#" + it.orderNumber.toString()
                resName_OD.text = it.restaurantName.toString()
                date_OD.text = convertDate(it.date as Long)
                if (it.table != null) {
                    table_OD.visibility = View.VISIBLE
                    mRootRef.child("/table/${it.restaurantID}/${it.table}/tableName")
                        .addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onCancelled(p0: DatabaseError) {

                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                table_OD.text = p0.value.toString()
                            }

                        })

                    table_ODT.visibility = View.VISIBLE
                } else {
                    table_OD.visibility = View.GONE
                    table_ODT.visibility = View.GONE
                }
                total_OD.text = it.total.toString() + " บาท"
            }

        })

        val adapterFood = FoodAdapter()
        recycleView_OD.adapter = adapterFood
        recycleView_OD.layoutManager = LinearLayoutManager(this)
        recycleView_OD.setHasFixedSize(true)

        val indicator = findViewById<StepperIndicator>(R.id.stepperIndicator)
//        val a = arrayOf("AAAAAAAAAAA","BBBBBBBBB","CCCCCCCCCCC","DDDDDDDDDD","EEEEEEEEEEEEEE")
//        indicator.setLabels(a)
        model.getMenu().observe(this, Observer {
            if (it != null) {
                val orderAmount = it.size
                var finish = 0
                it.forEach { orders ->
                    if (orders.second.finish!!) {
                        finish += 1
                    }
                }
                finish_OD.text = "$finish จาก $orderAmount รายการ"
                adapterFood.setData(it)
            }

        })
        //Toast.makeText(applicationContext, orderKey, Toast.LENGTH_LONG).show()


//        val adapterStatus = StatusAdapter()
//        recyclerView_status.adapter = adapterStatus
//        recyclerView_status.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
//        recyclerView_status.setHasFixedSize(true)


    }

    @SuppressLint("SimpleDateFormat")
    private fun convertDate(t: Long): String {
//        SimpleDateFormat("ddMMyyyy HH:mm:ss").format(Date(1536671117304)).toString()
        val d = Date(t)
        val orderDate = SimpleDateFormat("ddMMyyyy").format(d).toLong()
        val today = SimpleDateFormat("ddMMyyyy").format(Date()).toLong()
        val fmD = SimpleDateFormat("dd")
        val fmM = SimpleDateFormat("MM")
        val fmY = SimpleDateFormat("yyyy")
        val fmT = SimpleDateFormat("HH:mm")

        val day = fmD.format(d)
        val month = fmM.format(d)
        val year = (fmY.format(d).toInt() + 543).toString()
        val time = fmT.format(d)



        return if (orderDate == today) {
            "วันนี้ เวลา $time"
        } else {
            "วันที่ $day/$month/$year เวลา $time"
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}
