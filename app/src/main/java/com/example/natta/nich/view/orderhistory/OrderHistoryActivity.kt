package com.example.natta.nich.view.orderhistory

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.example.natta.nich.R
import com.example.natta.nich.viewmodel.OrderHistoryViewModel
import kotlinx.android.synthetic.main.activity_order_history.*

class OrderHistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_history)
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.title = "ประวัติการสั่งอาหาร"
        val model = ViewModelProviders.of(this).get(OrderHistoryViewModel::class.java)
        val adapter = OrderHistoryAdapter()
        recycleView_oh.adapter = adapter
        recycleView_oh.layoutManager = LinearLayoutManager(applicationContext)

        model.getOrder().observe(this, Observer {
            if (it != null) {
                adapter.setData(it)
            }
        })
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}
