package com.example.natta.nich.view.order

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
import kotlinx.android.synthetic.main.list_order.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.Comparator

@Suppress("DEPRECATION")
class OrderAdapter : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {
    private var order = arrayListOf<Pair<String, Order>>()
    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun getItemCount(): Int {
        return order.size

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val time = convertDate(order[position].second.date as Long)
        holder.res.text = order[position].second.restaurantName.toString()
        holder.time.text = time
        holder.status.text = order[position].second.status.toString()
        holder.total.text = order[position].second.total.toString() + " ฿"
        holder.orderNumber.text = order[position].second.orderNumber.toString()
        holder.cardOrder.setOnClickListener {
            val i = Intent(context.applicationContext, OrderDetailActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            i.putExtra("orderKey", order[position].first)
            context.startActivity(i)
        }
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
            "วันนี้\n$time"
        } else {
            "$day/$month/$year\n$time"
        }
    }


    fun setData(order: ArrayList<Pair<String, Order>>) {
        order.sortWith(Comparator { o1, o2 ->
            val a = o1!!.second.date as Long
            val b = o2!!.second.date as Long
            when {
                a < b -> 1
                a == b -> 0
                else -> -1
            }
        })
        this.order = order
        notifyDataSetChanged()
    }


    inner class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var res = view.resName_Order as TextView
        var time = view.time_order as TextView
        var status = view.status_order as TextView
        var total = view.total_order as TextView
        var orderNumber = view.orderNumber_order as TextView
        var cardOrder = view.cardOrder_order as CardView
    }
}