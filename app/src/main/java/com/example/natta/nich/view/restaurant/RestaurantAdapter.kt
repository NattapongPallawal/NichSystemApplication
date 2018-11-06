package com.example.natta.nich.view.restaurant

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.natta.nich.R
import com.example.natta.nich.data.Restaurant
import com.example.natta.nich.view.food.FoodActivity
import com.example.natta.nich.view.restaurantdetail.RestaurantDetailActivity
import kotlinx.android.synthetic.main.list_restaurant.view.*

class RestaurantAdapter(private var latitude: Double, private var longitude: Double) :
    RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder>() {
    private var restaurantList = listOf<Restaurant>()
    private lateinit var context: Context

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RestaurantViewHolder {
        context = p0.context
        val view = LayoutInflater.from(context).inflate(R.layout.list_restaurant, p0, false)
        return RestaurantViewHolder(view)
    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(p0: RestaurantViewHolder, p1: Int) {
        val distance = calculateDistance(
            restaurantList[p1].address!!.latitude,
            restaurantList[p1].address!!.longitude
        )
        Glide.with(context).load(restaurantList[p1].picture).apply(RequestOptions.errorOf(R.drawable.ic_error)  ).into(p0.imageView)

        // Picasso.get().load(restaurantList[p1].picture).into(p0.imageView)

        p0.resName.text = restaurantList[p1].restaurantName.toString()
        p0.rating.rating = restaurantList[p1].rating!!.toFloat()
        p0.resTime.text =  "เปิด ${restaurantList[p1].times} น."
        p0.resLocation.text = "$distance กม."

        p0.btnOrderFood.setOnClickListener {
            val i = Intent(context, FoodActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            i.putExtra("resKey", restaurantList[p1].getKey())
            context.startActivity(i)
        }
        p0.restaurantView.setOnClickListener {
            val i = Intent(context, RestaurantDetailActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            i.putExtra("restaurant", restaurantList[p1])
            i.putExtra("distance", distance)
            i.putExtra("resKey", restaurantList[p1].getKey())
            context.startActivity(i)
        }
    }

    private fun calculateDistance(latitude: Double?, longitude: Double?, fromSetData: Boolean = false): String {
        val resLocation = Location("resLocation")
        resLocation.longitude = longitude!!
        resLocation.latitude = latitude!!
        val currentLocation = Location("currentLocation")
        currentLocation.longitude = this.longitude
        currentLocation.latitude = this.latitude
        val distance = currentLocation.distanceTo(resLocation) / 1000
        return if (fromSetData) distance.toString() else String.format("%.1f", distance)
    }

    fun setData(restaurantList: ArrayList<Restaurant>) {
        restaurantList.sortWith(Comparator { o1, o2 ->
            val a = calculateDistance(o1!!.address!!.latitude, o1.address!!.longitude, true).toFloat()
            val b = calculateDistance(o2!!.address!!.latitude, o2.address!!.longitude, true).toFloat()
            when {
                a > b -> 1
                a == b -> 0
                else -> -1
            }
        })
        this.restaurantList = restaurantList
        notifyDataSetChanged()
    }

    inner class RestaurantViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imageView = view.imageView_res as ImageView
        var resName = view.resName_res as TextView
        var resTime = view.resTime_res as TextView
        var resLocation = view.resLocation_res as TextView
        var rating = view.rating_res as RatingBar
        var btnOrderFood = view.btnOrderFood_res as TextView
        var restaurantView = view.restaurantView as CardView
    }
}