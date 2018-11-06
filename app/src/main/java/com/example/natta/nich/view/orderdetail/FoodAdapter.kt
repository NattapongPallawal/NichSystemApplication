package com.example.natta.nich.view.orderdetail

import android.annotation.SuppressLint
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.example.natta.nich.R
import com.example.natta.nich.data.OrderMenu
import kotlinx.android.synthetic.main.list_payment.view.*

class FoodAdapter : RecyclerView.Adapter<FoodAdapter.FoodOrderDetailViewHolder>() {
    private var menu = arrayListOf<Pair<String, OrderMenu>>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodOrderDetailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_payment, parent, false)
        return FoodOrderDetailViewHolder(view)
    }

    override fun getItemCount(): Int {
        return menu.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: FoodOrderDetailViewHolder, position: Int) {
        if(menu[position].second.canDo != null){
            if (menu[position].second.canDo!!){
                holder.foodName.setTextColor(Color.GRAY)
            }else{
                holder.foodName.setTextColor(Color.RED)
            }
        }

        holder.amount.text = menu[position].second.amount.toString()
        holder.foodName.text =
                "${menu[position].second.foodName.toString()} " +
                "${if (menu[position].second.foodTypeName != null) menu[position].second.foodTypeName.toString() else ""} " +
                menu[position].second.foodSizeName.toString()
        holder.price.text = menu[position].second.price.toString()
        if (menu[position].second.finish!!) {
            holder.done.visibility = View.VISIBLE
            holder.done.playAnimation()
        } else {
            holder.done.visibility = View.GONE
        }

    }


    fun setData(menu: ArrayList<Pair<String, OrderMenu>>) {
        this.menu = menu
        notifyDataSetChanged()
    }

    inner class FoodOrderDetailViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var amount = view.amount_PML as TextView
        var foodName = view.foodName_PML as TextView
        var price = view.price_PML as TextView
        var done = view.foodDone_OD as LottieAnimationView
    }
}