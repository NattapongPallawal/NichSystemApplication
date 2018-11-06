package com.example.natta.nich.view.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.text.style.RelativeSizeSpan
import android.util.Log
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
import com.example.natta.nich.view.restaurantdetail.RestaurantDetailActivity
import kotlinx.android.synthetic.main.list_restaurant_home.view.*

class NearbyResAdapter(private var latitude: Double, private var longitude: Double) :
    RecyclerView.Adapter<NearbyResAdapter.HomeViewHolder>() {
    private var restaurants = arrayListOf<Pair<String, Restaurant>>()
    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_restaurant_home, parent, false)
        return HomeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return restaurants.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.resName.text = restaurants[position].second.restaurantName
        holder.rating.rating = restaurants[position].second.rating!!.toFloat()
        holder.distance.text = calculateDistance(
            restaurants[position].second.address!!.latitude,
            restaurants[position].second.address!!.longitude
        ) + " กม."
        Glide.with(context)
            .load(restaurants[position].second.picture)
//            .load("https://www.androidhive.info/wp-content/uploads/2016/11/android-m-permissions-requesting-in-fragment.png")
            .apply(RequestOptions.errorOf(R.drawable.ic_error)  )
            .into(holder.img)

        holder.res_home.setOnClickListener {
            val i = Intent(context, RestaurantDetailActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            i.putExtra("restaurant", restaurants[position].second)
            i.putExtra("distance", calculateDistance(
                restaurants[position].second.address!!.latitude,
                restaurants[position].second.address!!.longitude
            ))
            i.putExtra("resKey", restaurants[position].first)
            context.startActivity(i)
        }
    }

    fun setData(restaurants: ArrayList<Pair<String, Restaurant>>) {
        restaurants.sortWith(Comparator { o1, o2 ->
            val a = calculateDistance(o1!!.second.address!!.latitude, o1.second.address!!.longitude, true).toFloat()
            val b = calculateDistance(o2!!.second.address!!.latitude, o2.second.address!!.longitude, true).toFloat()
            when {
                a > b -> 1
                a == b -> 0
                else -> -1
            }
        })
        val temp = arrayListOf<Pair<String, Restaurant>>()
        for (i in 0..4) {
            if (i < restaurants.size) {
                temp.add(restaurants[i])
            }

        }
        this.restaurants = temp
        notifyDataSetChanged()
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

    inner class HomeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var resName = view.res_name_homeF as TextView
        var rating = view.ratingBar_home as RatingBar
        var distance = view.distance_home as TextView
        var img = view.img_res_home as ImageView
        var res_home = view.res_home as ConstraintLayout
    }
}