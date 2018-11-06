package com.example.natta.nich.view.favorite.food

import android.content.Context
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
import com.example.natta.nich.data.FavFood
import com.example.natta.nich.data.Food
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.list_food_fav.view.*
import kotlinx.android.synthetic.main.list_header_fav_food.view.*


class FavoriteFoodAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var favFood = arrayListOf<Pair<String, FavFood?>>()
    private lateinit var context: Context

    private val TYPE_HEADER = 0
    private val TYPE_ITEM = 1
    private var mRootRef = FirebaseDatabase.getInstance().reference



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.list_header_fav_food, parent, false)
                context = parent.context
                FavoriteFoodHeaderViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.list_food_fav, parent, false)
                context = parent.context
                FavoriteFoodViewHolder(view)
            }
        }

    }

    override fun getItemCount(): Int {
        return favFood.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is FavoriteFoodHeaderViewHolder) {
            val h: FavoriteFoodHeaderViewHolder = holder
            h.res.text = favFood[position].first
            if (true)
                h.div.visibility = View.GONE
        } else if (holder is FavoriteFoodViewHolder) {
            val h: FavoriteFoodViewHolder = holder
//            h.foodName.text = favFood[position].second!!.foodName
//            h.rating.rating = favFood[position].second!!.rating!!.toFloat()

            mRootRef.child("menu/${favFood[position].second!!.resID}/${favFood[position].second!!.foodID}").addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    val food = p0.getValue(Food::class.java)
                    h.foodName.text = food?.foodName.toString()
                    h.rating.rating = food?.rate!!.toFloat()
                    Glide.with(context)
                        .load(food.picture)
                        .apply(RequestOptions.errorOf(R.drawable.ic_error))
//                    .apply(RequestOptions.bitmapTransform(ColorFilterTransformation(Color.argb(70,0,0,0))))
                        .into(h.img)
                }

            })

        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (favFood[position].second != null) {
            TYPE_ITEM
        } else {
            TYPE_HEADER
        }
    }

    fun setData(favFood: ArrayList<Pair<String, FavFood>>) {
        var CR = ""
        val temp = arrayListOf<Pair<String, FavFood?>>()
        favFood.sortWith(Comparator { o1, o2 ->
            val a = o1!!.second.resID!!
            val b = o2!!.second.resID!!
            when {
                a > b -> 1
                a == b -> 0
                else -> -1
            }
        })
        favFood.forEach {
            if (CR != it.second.resID) {
                CR = it.second.resID!!
                temp.add(Pair(it.second.resName.toString(), null))
                temp.add(it)
            } else {
                temp.add(it)
            }
        }
        Log.d("numberRestaurant", "${temp.size}")

        this.favFood = temp
        notifyDataSetChanged()
    }

    inner class FavoriteFoodViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var img = view.picture_FavFood as ImageView
        var foodName = view.foodName_FavFood as TextView
        var rating = view.rating_FF as RatingBar
    }

    inner class FavoriteFoodHeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var res = view.res_header_fav_food as TextView
        var div = view.div_FF as View
    }
}