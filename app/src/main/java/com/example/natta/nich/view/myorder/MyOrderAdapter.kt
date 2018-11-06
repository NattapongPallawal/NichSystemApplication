package com.example.natta.nich.view.myorder

import android.annotation.SuppressLint
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.natta.nich.R
import com.example.natta.nich.data.Select
import com.example.natta.nich.view.fooddetail.FoodDetailActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.list_my_order.view.*

class MyOrderAdapter() : RecyclerView.Adapter<MyOrderAdapter.MyOrderViewHolder>() {
    private var myOrder = arrayListOf<Pair<String, Select>>()
    private var mRootRef = FirebaseDatabase.getInstance().reference
    private var mAuth = FirebaseAuth.getInstance()
    private var total = MutableLiveData<Double>()
    private var t: Double = 0.0
    private lateinit var context: Context

    init {
        total.value = 0.0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyOrderViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.list_my_order, parent, false)

        return MyOrderViewHolder(view)
    }

    override fun getItemCount(): Int {
        return myOrder.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyOrderViewHolder, position: Int) {
        var amount: Int = myOrder[position].second.amount!!.toInt()
        if (!myOrder[position].second.foodTypeName.isNullOrEmpty()) {
            holder.foodName.text = myOrder[position].second.foodName.toString() + " " +
                    myOrder[position].second.foodTypeName.toString()
        } else {
            holder.foodName.text = myOrder[position].second.foodName.toString()
        }
        holder.foodSize.text = myOrder[position].second.foodSizeName.toString()
        holder.amount.text = myOrder[position].second.amount.toString()
        holder.price.text = myOrder[position].second.price.toString() + " ฿"

        Glide.with(context).load(myOrder[position].second.picture).apply(RequestOptions.errorOf(R.drawable.ic_error)  ).into(holder.picture)



        holder.add.setOnClickListener {
            amount = addAmount(amount)
            mRootRef.child("temp/cart/${mAuth.currentUser!!.uid}/select/${myOrder[position].first}/amount")
                .setValue(amount)
        }
        holder.remove.setOnClickListener {
            amount = removeAmount(amount)
            mRootRef.child("temp/cart/${mAuth.currentUser!!.uid}/select/${myOrder[position].first}/amount")
                .setValue(amount)

        }


        holder.item.setOnClickListener {
            val i = Intent(context.applicationContext, FoodDetailActivity::class.java)
            i.putExtra("foodKey", myOrder[position].second.foodID)
            i.putExtra("resKey", myOrder[position].second.resID)
            i.putExtra("select", myOrder[position].second)
            i.putExtra("selectKey", myOrder[position].first)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(i)
        }

    }

    fun removeAt(position: Int) {
        val key = myOrder[position].first
        myOrder.removeAt(position)
        notifyItemRemoved(position)
        Delay(key).execute()

    }

    private fun addAmount(a: Int): Int {
        var amount: Int = a
        if (checkAmount(amount)) {
            if (amount != 99)
                amount = ++amount
        }
        return amount
    }

    private fun removeAmount(a: Int): Int {
        var amount: Int = a
        if (checkAmount(amount)) {
            if (amount != 1)
                amount = --amount
        }
        return amount
    }

    private fun checkAmount(amount: Int): Boolean {
        if (amount in 1..99) {
            return true
        }
        return false
    }

    fun setData(myOrder: ArrayList<Pair<String, Select>>) {
        this.t = 0.0
        this.myOrder = myOrder

        this.myOrder.forEach {
            t += it.second.amount!! * it.second.price!!
        }
        this.total.value = t
        notifyDataSetChanged()
    }

    fun getTotal(): MutableLiveData<Double> {
        return total
    }


    inner class MyOrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var price = view.price_MO as TextView
        var foodSize = view.foodSize_MO as TextView
        var foodName = view.foodName_MO as TextView
        var amount = view.amount_MO as TextView
        var picture = view.picture_MO as ImageView
        var add = view.addAmount_MO as ImageView
        var remove = view.removeAmount_MO as ImageView
        var item = view.item_food_MO as CardView

    }

    inner class Delay(var key: String) : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg p0: Void?): Void? {
            Thread.sleep(500)
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            mRootRef.child("temp/cart/${mAuth.currentUser!!.uid}/select/$key").setValue(null)
        }

    }

}