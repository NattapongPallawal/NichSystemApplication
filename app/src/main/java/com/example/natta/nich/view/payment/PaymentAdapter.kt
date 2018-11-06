package com.example.natta.nich.view.payment

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.natta.nich.R
import com.example.natta.nich.data.Select
import kotlinx.android.synthetic.main.list_payment.view.*

class PaymentAdapter(var context: Context) : RecyclerView.Adapter<PaymentAdapter.PaymentViewHolder>() {
    private var myOrder = arrayListOf<Pair<String, Select>>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_payment, parent, false)
        return PaymentViewHolder(view)
    }

    override fun getItemCount(): Int {
        return myOrder.size
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        holder.amount.text = myOrder[position].second.amount.toString()
        holder.foodName.text = if (myOrder[position].second.foodTypeName != null) {
            "${myOrder[position].second.foodName} ${myOrder[position].second.foodTypeName} ${myOrder[position].second.foodSizeName}"
        } else {
            "${myOrder[position].second.foodName} ${myOrder[position].second.foodSizeName}"
        }
        holder.price.text = myOrder[position].second.price.toString()
    }

    fun setData(myOrder: ArrayList<Pair<String, Select>>) {
        this.myOrder = myOrder
        notifyDataSetChanged()
    }

    inner class PaymentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var amount = view.amount_PML as TextView
        var foodName = view.foodName_PML as TextView
        var price = view.price_PML as TextView

    }
}