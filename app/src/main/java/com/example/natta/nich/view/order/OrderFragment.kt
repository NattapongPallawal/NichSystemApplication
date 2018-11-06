package com.example.natta.nich.view.order

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.natta.nich.R
import com.example.natta.nich.viewmodel.OrderViewModel
import kotlinx.android.synthetic.main.order_fragment.*


class OrderFragment : Fragment() {

    companion object {
        fun newInstance() = OrderFragment()
    }

    private lateinit var viewModel: OrderViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.order_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(OrderViewModel::class.java)
        val adapter = OrderAdapter()
        recyclerView_orderFm.adapter = adapter
        recyclerView_orderFm.layoutManager = LinearLayoutManager(context)

        viewModel.getOrder().observe(this, Observer {
            if (it != null) {
                adapter.setData(it)
            }
        })
    }

}
