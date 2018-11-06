package com.example.natta.nich.view.favorite

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.example.natta.nich.view.favorite.food.FavoriteFoodFragment
import com.example.natta.nich.view.favorite.restaurant.FavoriteRestaurantFragment

class FavoriteFoodTabPager (fm: FragmentManager) : FragmentPagerAdapter(fm){
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                FavoriteFoodFragment()
            }
            1 -> {
                FavoriteRestaurantFragment()
            }
            else -> {
                FavoriteFoodFragment()
            }
        }

    }

    override fun getCount(): Int {
        return 2
    }
}