package com.example.natta.nich.view.favorite.restaurant

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.natta.nich.R
import kotlinx.android.synthetic.main.favorite_restaurant_fragment.*

class FavoriteRestaurantFragment : Fragment() {

    companion object {
        fun newInstance() = FavoriteRestaurantFragment()
    }

    private lateinit var viewModel: FavoriteRestaurantViewModel
    private val adapter = FavoriteRestaurantAdapter()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.favorite_restaurant_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(FavoriteRestaurantViewModel::class.java)
        recyclerView_res_fav.adapter = adapter
        recyclerView_res_fav.layoutManager = LinearLayoutManager(context)
        viewModel.getFavRes().observe(this, Observer {
            if (it != null) {
                adapter.setData(it)
            }
        })
    }

}
