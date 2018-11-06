package com.example.natta.nich.view.restaurantdetail

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.Window
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.natta.nich.R
import com.example.natta.nich.data.Restaurant
import com.example.natta.nich.viewmodel.RestaurantDetailViewModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.activity_restaurant_detail.*
import kotlinx.android.synthetic.main.content_restaurant_detail.*
import kotlinx.android.synthetic.main.custom_dialog_feedback.*

class RestaurantDetailActivity : AppCompatActivity() {
    private var restaurant = Restaurant()
    private var resKey = ""
    private var distance = "0"
    private var model = RestaurantDetailViewModel()
    private lateinit var dialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_detail)
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.title = "รายละเอียดร้านค้า" // android:label="รายละเอียดร้านค้า"

        model = ViewModelProviders.of(this).get(RestaurantDetailViewModel::class.java)

        Dexter.withActivity(this)
            .withPermission(Manifest.permission.CALL_PHONE)
            .withListener(
                object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                        if (response != null) {
//                                    Toast.makeText(applicationContext, response.permissionName, Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permission: PermissionRequest?,
                        token: PermissionToken?
                    ) {
                        token?.continuePermissionRequest()
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                        val dialogPermissionListener = DialogOnDeniedPermissionListener.Builder
                            .withContext(this@RestaurantDetailActivity)
                            .withTitle("การเข้าถึงโทรศัท์")
                            .withMessage("ต้องการเข้าถึงโทรศัพท์เพื่อบโทรออก")
                            .withButtonText("Ok")
                            .withIcon(R.mipmap.ic_launcher)
                            .build()
                        dialogPermissionListener.onPermissionDenied(response)
//                                finish()

                    }
                }
            ).onSameThread()
            .check()

        restaurant = intent.getParcelableExtra("restaurant")
        distance = intent.getStringExtra("distance")
        resKey = intent.getStringExtra("resKey")

        model.setData(resKey, restaurant)

        val adapter = FeedbackAdapter()
        setDialog()
        loadImage()
        setTextValue()
        btn_review_RD.setOnClickListener {
            dialog.show()
        }
        recycleView_feedback.adapter = adapter
        recycleView_feedback.layoutManager = LinearLayoutManager(applicationContext)

        model.getFeedback(resKey).observe(this, Observer {
            if (it != null) {
                adapter.setData(it)
            }
        })

        fabFavorite_RD.setOnClickListener { view ->
            model.addFavoriteRes()
            Snackbar.make(view, "เพิ่ม ${restaurant.restaurantName} \nลงในรายการโปรเรียบร้อยแล้ว", Snackbar.LENGTH_LONG)
                .show()
        }
        resBtnCall_RD.setOnClickListener {

            val i = Intent(Intent.ACTION_CALL)
            i.data = Uri.parse("tel:${restaurant.phoneNumber}")
            startActivity(i)
        }
        resBtnDirection_RD.setOnClickListener {
            val geo =
                Uri.parse("geo:${restaurant.address!!.latitude},${restaurant.address!!.longitude}?q=${restaurant.restaurantName}")
            val i = Intent(Intent.ACTION_VIEW, geo)
            i.setPackage("com.google.android.apps.maps")
            startActivity(i)
        }
    }

    private fun setDialog() {
        var rating: Float = 0f
        var title = ""
        var review = ""
        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.custom_dialog_feedback)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.window.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
        dialog.resName_DI_RD.text = restaurant.restaurantName
        dialog.ratingBar_DI_RD.setOnRatingBarChangeListener { _, r, _ ->
            rating = r
        }
        dialog.title_RD.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                title = s.toString()
            }

        })
        dialog.review_FB_RD.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                review = s.toString()
            }

        })
        dialog.btn_send_FB_RD.setOnClickListener {
            model.setFeedback(rating, title, review, resKey)
            dialog.title_RD.text.clear()
            dialog.review_FB_RD.text.clear()
            dialog.ratingBar_DI_RD.rating = 0f
            dialog.dismiss()
        }
    }

    private fun loadImage() {
        Glide.with(this).load(restaurant.picture).apply(RequestOptions.errorOf(R.drawable.ic_error)).into(resImage_RD)

    }

    @SuppressLint("SetTextI18n")
    private fun setTextValue() {
        resName_RD.text = restaurant.restaurantName.toString()
        resLocation_RD.text = try {
            restaurant.address!!.address.toString()
        } catch (e: KotlinNullPointerException) {
            "ไม่พบ"
        }
//        var date = C()
//        Log.d("setTextValueDate",date)
        resOpen_RD.text = "เปิด ${restaurant.times} น."
        resDistance_RD.text = "$distance  กม."
        resRating_RD.rating = restaurant.rating!!.toFloat()
        resCall_RD.text = restaurant.phoneNumber.toString()
        resAbout_RD.text = restaurant.about.toString()
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}
