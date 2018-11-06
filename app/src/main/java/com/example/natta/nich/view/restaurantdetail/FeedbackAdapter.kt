package com.example.natta.nich.view.restaurantdetail

import android.annotation.SuppressLint
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
import com.example.natta.nich.R
import com.example.natta.nich.data.Customer
import com.example.natta.nich.data.Feedback
import com.google.firebase.database.*

import kotlinx.android.synthetic.main.list_feedback.view.*
import java.text.SimpleDateFormat
import java.util.*

class FeedbackAdapter() : RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder>() {
    private var feedback = arrayListOf<Feedback>()
    private var customerData = RestaurantDetailActivity()
    private var mRootRef = FirebaseDatabase.getInstance().reference
    private lateinit var context: Context

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): FeedbackViewHolder {
        context = p0.context
        val view = LayoutInflater.from(context).inflate(R.layout.list_feedback, p0, false)
        return FeedbackViewHolder(view)
    }

    override fun getItemCount(): Int {
        return feedback.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(h: FeedbackViewHolder, @SuppressLint("RecyclerView") p1: Int) {
        val mCustomerRef = mRootRef.child("customer").child(feedback[p1].customer!!)
        val listener = object : ValueEventListener {
            var mOneCustomer: Customer? = null
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                mOneCustomer = p0.getValue(Customer::class.java)
                h.name.text = "${mOneCustomer?.firstName} ${mOneCustomer?.lastName}"
                try {
                    Glide.with(context).load(mOneCustomer?.picture).into(h.image)

                } catch (e: IllegalArgumentException) {

                }


            }
        }
        try {
            h.date.text = convertDate(feedback[p1].date as Long)
        } catch (e: TypeCastException) {
        }

        mCustomerRef.addValueEventListener(listener)
        h.title.text = feedback[p1].title.toString()
        h.comment.text = feedback[p1].comment.toString()
        h.rating.rating = feedback[p1].rating!!.toFloat()
    }

    @SuppressLint("SimpleDateFormat")
    private fun convertDate(t: Long): String {
//        SimpleDateFormat("ddMMyyyy HH:mm:ss").format(Date(1536671117304)).toString()
        val d = Date(t)
        val orderDate = SimpleDateFormat("ddMMyyyy").format(d).toLong()
        val today = SimpleDateFormat("ddMMyyyy").format(Date()).toLong()
        val fmD = SimpleDateFormat("dd")
        val fmM = SimpleDateFormat("MM")
        val fmY = SimpleDateFormat("yyyy")
        val fmT = SimpleDateFormat("HH:mm")

        val day = fmD.format(d)
        val month = fmM.format(d)
        val year = (fmY.format(d).toInt() + 543).toString()
        val time = fmT.format(d)



        return if (orderDate == today) {
            "วันนี้\n$time"
        } else {
            "$day/$month/$year\n$time"
        }
    }

    fun setData(feedback: ArrayList<Feedback>) {
        feedback.sortWith(Comparator { o1, o2 ->
            val a = o1.rating!!
            val b = o2.rating!!
            when {
                a < b -> 1
                a == b -> 0
                else -> -1
            }
        })
        this.feedback = feedback
        Log.d("timeStampMap", "${ServerValue.TIMESTAMP}")
        notifyDataSetChanged()
    }


    inner class FeedbackViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name = view.feedbackName_RD as TextView
        var title = view.feedbackTittle_RD as TextView
        var comment = view.feedbackComment_RD as TextView
        var image = view.feedbackImg_RD as ImageView
        var rating = view.feedbackRating_RD as RatingBar
        var date = view.date_FB as TextView
    }
}