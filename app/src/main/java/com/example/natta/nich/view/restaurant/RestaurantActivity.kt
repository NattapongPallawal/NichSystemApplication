package com.example.natta.nich.view.restaurant

import android.Manifest
import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.example.natta.nich.R
import com.example.natta.nich.viewmodel.RestaurantViewModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.activity_restaurant.*

class RestaurantActivity : AppCompatActivity() {
    private var latitude = 0.0
    private var longitude = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant)
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.title = "ร้านอาหาร"
        checkPermission()
        val model = ViewModelProviders.of(this).get(RestaurantViewModel::class.java)
        getLocation()
        val adapter = RestaurantAdapter(latitude, longitude)
        recyclerView_restaurant.adapter = adapter
        recyclerView_restaurant.layoutManager = LinearLayoutManager(applicationContext)

        model.getRestaurant().observe(this, Observer {
            if (it != null) {
                adapter.setData(it)
            }
        })
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun checkPermission() {
        Dexter.withActivity(this)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
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
                            .withContext(this@RestaurantActivity)
                            .withTitle("การเข้าถึงที่อยู่ปัจจุบัน")
                            .withMessage("เราต้องการที่อยู่ปัจจุบันของคุณ")
                            .withButtonText("Ok")
                            .withIcon(R.mipmap.ic_launcher)
                            .build()
                        dialogPermissionListener.onPermissionDenied(response)
                        finish()

                    }
                }
            )
            .onSameThread()
            .check()
    }
    @SuppressLint("MissingPermission")
    fun getLocation() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        val location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        val location2 = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)

        when {
            location != null -> {
                latitude = location.latitude
                longitude = location.longitude
//                Toast.makeText(this, "$longitude , $latitude", Toast.LENGTH_SHORT).show()


            }
            location1 != null -> {
                latitude = location1.latitude
                longitude = location1.longitude
//                Toast.makeText(this, "$longitude , $latitude", Toast.LENGTH_SHORT).show()


            }
            location2 != null -> {
                latitude = location2.latitude
                longitude = location2.longitude
//                Toast.makeText(this, "$longitude , $latitude", Toast.LENGTH_SHORT).show()

            }
            else -> Toast.makeText(this, "Unble to Trace your location", Toast.LENGTH_SHORT).show()
        }

    }
}
