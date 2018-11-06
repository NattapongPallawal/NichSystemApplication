package com.example.natta.nich.view.favorite.restaurant

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
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
import com.example.natta.nich.data.FavRes
import com.example.natta.nich.data.Restaurant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import jp.wasabeef.glide.transformations.ColorFilterTransformation
import kotlinx.android.synthetic.main.list_restaurant_fav.view.*

class FavoriteRestaurantAdapter:RecyclerView.Adapter<FavoriteRestaurantAdapter.FavoriteRestaurantViewHolder>() {
    private var favRes = arrayListOf<Pair<String, FavRes>>()
    private lateinit var context : Context
    private var mRootRef = FirebaseDatabase.getInstance().reference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteRestaurantViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.list_restaurant_fav, parent,false)
        return FavoriteRestaurantViewHolder(view)

    }

    override fun getItemCount(): Int {

        return favRes.size
    }

    override fun onBindViewHolder(holder: FavoriteRestaurantViewHolder, position: Int) {



        mRootRef.child("restaurant/${favRes[position].second.resID}")
            .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val res = p0.getValue(Restaurant::class.java)!!
                Glide.with(context)
                    .load(res.picture)
                    .apply(RequestOptions.errorOf(R.drawable.ic_error)  )
                    .apply(RequestOptions.bitmapTransform(ColorFilterTransformation(Color.argb(70,0,0,0))))
                    .into(holder.picture)
                holder.resName.text = res.restaurantName
                holder.rating.rating = res.rating!!.toFloat()

            }


        })


    }

    fun setData(favRes: ArrayList<Pair<String, FavRes>>) {
        this.favRes = favRes
        notifyDataSetChanged()
    }

    inner class FavoriteRestaurantViewHolder(view: View) : RecyclerView.ViewHolder(view){
        var resName = view.resName_FR as TextView
        var picture = view.resPic_FR as ImageView
        var rating = view.ratingRes_FR as RatingBar

    }
}