package com.example.natta.nich.view.orderhistory

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.natta.nich.R
import com.example.natta.nich.data.Order
import com.example.natta.nich.view.orderdetail.OrderDetailActivity
import kotlinx.android.synthetic.main.list_order_history.view.*
import java.text.SimpleDateFormat
import java.util.*

class OrderHistoryAdapter() : RecyclerView.Adapter<OrderHistoryAdapter.OrderHistoryHolder>() {
    private var orderList = arrayListOf<Pair<String, Order>>()
    private lateinit var context: Context
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): OrderHistoryHolder {
        context = p0.context
        val view = LayoutInflater.from(context).inflate(R.layout.list_order_history, p0, false)
        return OrderHistoryHolder(view)

    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(p0: OrderHistoryHolder, p1: Int) {
        p0.resName.text = orderList[p1].second.restaurantName.toString()
        p0.amountMenu.text = orderList[p1].second.totalMenu.toString() + " รายการ"
        p0.date.text = convertDate(orderList[p1].second.date as Long)
        p0.price.text = orderList[p1].second.total.toString() + " ฿"
        p0.orderID.text = orderList[p1].second.orderNumber.toString()

        p0.orderHistory.setOnClickListener {
            val i = Intent(context, OrderDetailActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            i.putExtra("orderKey", orderList[p1].first)
            context.startActivity(i)
        }

    }

    fun setData(orders: ArrayList<Pair<String, Order>>) {
        orders.sortWith(Comparator { o1, o2 ->
            val a = o1!!.second.date as Long
            val b = o2!!.second.date as Long
            when {
                a < b -> 1
                a == b -> 0
                else -> -1
            }
        })
        orderList = orders
        notifyDataSetChanged()
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
            "วันที่ $day/$month/$year\nเวลา $time"
        }
    }

    inner class OrderHistoryHolder(view: View) : RecyclerView.ViewHolder(view) {
        var resName = view.resName_oh as TextView
        var amountMenu = view.amountMenu_oh as TextView
        var date = view.date_oh as TextView
        var price = view.price_oh as TextView
        var orderID = view.orderID as TextView
        var orderHistory = view.order_history as CardView
    }
}