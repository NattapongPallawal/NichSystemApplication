package com.example.natta.nich.view.home

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.natta.nich.R
import com.example.natta.nich.view.favorite.FavoriteActivity
import com.example.natta.nich.view.orderdetail.OrderDetailActivity
import com.example.natta.nich.view.randomfood.RandomFoodActivity
import com.example.natta.nich.viewmodel.HomeViewModel
import jp.wasabeef.glide.transformations.ColorFilterTransformation
import kotlinx.android.synthetic.main.home_fragment.*


class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private lateinit var viewModel: HomeViewModel

    private var latitude = 0.0
    private var longitude = 0.0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        latitude = arguments!!.getDouble("latitude")
        longitude = arguments!!.getDouble("longitude")

        val adapterNearby = NearbyResAdapter(latitude, longitude)
        val adapterPopular = PopularResAdapter(latitude, longitude)
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)


        initNearbyResView(adapterNearby)
        initPopularResView(adapterPopular)
        viewModel.getNearbyRes().observe(this, Observer {

            if (it != null) {
                adapterNearby.setData(it)
            }
        })

        viewModel.getPopularRes().observe(this, Observer {
            if (it != null) {
                adapterPopular.setData(it)
            }
        })


        random_home.setOnClickListener {
            startActivity(Intent(this.context, RandomFoodActivity::class.java))
        }
        cart_home.setOnClickListener {
            startActivity(Intent(this.context, FavoriteActivity::class.java))
        }
        recent_home.setOnClickListener {
            if (viewModel.ready.value!!.first) {
                val key = viewModel.getOrder().value!!.first
                val i = Intent(this.context, OrderDetailActivity::class.java)
                i.putExtra("orderKey", key)
                startActivity(i)
            }

        }

        viewModel.getMenu().observe(this, Observer {
            if (it != null) {
                Glide.with(this.context!!)
                    .load(it.second.picture)
                    .apply(RequestOptions.bitmapTransform(ColorFilterTransformation(Color.argb(70, 0, 0, 0))))
                    .apply(RequestOptions.errorOf(R.drawable.ic_error)  )
                    .into(img_food_recent_home)
                Log.d("getLastMenu", it.second.picture)
                foodName_recent_home.text = it.second.foodName
            }
        })

    }

    override fun onStop() {
        super.onStop()

    }
    private fun initNearbyResView(adapterNearby: NearbyResAdapter) {
        recycleView_resnearby_home.isNestedScrollingEnabled = false
        recycleView_resnearby_home.setHasFixedSize(false)
        recycleView_resnearby_home.adapter = adapterNearby
        recycleView_resnearby_home.layoutManager = LinearLayoutManager(this.context)
    }

    private fun initPopularResView(adapterPopular: PopularResAdapter) {
        recycleView_resTop_home.isNestedScrollingEnabled = false
        recycleView_resTop_home.setHasFixedSize(false)
        recycleView_resTop_home.adapter = adapterPopular
        recycleView_resTop_home.layoutManager = LinearLayoutManager(this.context)
    }
}


