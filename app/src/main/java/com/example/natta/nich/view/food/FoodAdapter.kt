package com.example.natta.nich.view.food

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.natta.nich.R
import com.example.natta.nich.data.Food
import com.example.natta.nich.view.fooddetail.FoodDetailActivity
import kotlinx.android.synthetic.main.list_food.view.*

class FoodAdapter() : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {
    private var food = arrayListOf<Pair<String, Food>>()
    private var resKey: String = ""
    private lateinit var onItemClickListener: OnItemClickListener
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.list_food, parent, false)
        return FoodViewHolder(view)
    }

    override fun getItemCount(): Int {
        return food.size

    }

    fun setOnItemClickListener(l: OnItemClickListener) {
        this.onItemClickListener = l
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.name.text = food[position].second.foodName
        holder.rating.rating = food[position].second.rate!!.toFloat()
        holder.price.text = food[position].second.price.toString() + " ฿"
        Glide.with(context).load(food[position].second.picture).apply(RequestOptions.errorOf(R.drawable.ic_error)  ).into(holder.picture)



        holder.cardFood.setOnClickListener {
            val i = Intent(context.applicationContext, FoodDetailActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            i.putExtra("food", food[position].second)
            i.putExtra("foodKey", food[position].first)
            i.putExtra("resKey", resKey)
            context.startActivity(i)
        }

        holder.addToCart.setOnClickListener {
            onItemClickListener.onItemClick(position, holder.addAni, holder.addToCart)

        }


    }


    fun setData(food: ArrayList<Pair<String, Food>>?, resKey: String) {
        this.food = food!!
        this.resKey = resKey
        notifyDataSetChanged()
    }

    inner class FoodViewHolder(view: View) : RecyclerView.ViewHolder(view) {


        var cardFood = view.cardFood as CardView
        var name = view.foodName_food as TextView
        var rating = view.rating_food as RatingBar
        var price = view.price_food as TextView
        var picture = view.picture_food as ImageView
        var addToCart = view.add_to_cart as ImageView
        var addAni = view.addAnimation as LottieAnimationView
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, addAni: LottieAnimationView, addToCart: ImageView)
    }


}