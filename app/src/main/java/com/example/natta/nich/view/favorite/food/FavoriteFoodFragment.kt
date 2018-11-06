package com.example.natta.nich.view.favorite.food

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.natta.nich.R
import kotlinx.android.synthetic.main.favorite_food_fragment.*

class FavoriteFoodFragment : Fragment() {
    private var adapter: FavoriteFoodAdapter = FavoriteFoodAdapter()

    companion object {
        fun newInstance() = FavoriteFoodFragment()
    }

    private lateinit var viewModel: FavoriteFoodViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.favorite_food_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(FavoriteFoodViewModel::class.java)
        viewModel.getFavFood().observe(this, Observer {
            if (it != null)
                adapter.setData(it)
        })
        recyclerView_fav_food.adapter = adapter
        recyclerView_fav_food.layoutManager = LinearLayoutManager(this.context)
    }

}
