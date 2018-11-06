package com.example.natta.nich.view.notification

import android.app.NotificationManager
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.NotificationCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.natta.nich.R
import com.example.natta.nich.data.Notification
import com.example.natta.nich.viewmodel.NotificationViewModel
import kotlinx.android.synthetic.main.notification_fragment.*


class NotificationFragment : Fragment() {

    companion object {
        fun newInstance() = NotificationFragment()
    }

    private lateinit var viewModel: NotificationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.notification_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val adapter = NotificationAdapter()
        recyclerView_notiFm.adapter = adapter
        recyclerView_notiFm.layoutManager = LinearLayoutManager(view!!.context)
        viewModel = ViewModelProviders.of(this).get(NotificationViewModel::class.java)
        viewModel.getNotification().observe(this, Observer {
            if (it != null) {
                adapter.setData(it as ArrayList<Notification>)


            }
        })
    }

}
