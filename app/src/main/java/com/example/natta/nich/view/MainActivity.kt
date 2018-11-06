package com.example.natta.nich.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.PorterDuff
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.TabLayout
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.natta.nich.R
import com.example.natta.nich.data.Customer
import com.example.natta.nich.view.aboutapplication.AboutApplicationActivity
import com.example.natta.nich.view.favorite.FavoriteActivity
import com.example.natta.nich.view.login.LoginActivity
import com.example.natta.nich.view.myprofile.MyProfileActivity
import com.example.natta.nich.view.orderhistory.OrderHistoryActivity
import com.example.natta.nich.view.osl.OSLActivity
import com.example.natta.nich.view.randomfood.RandomFoodActivity
import com.example.natta.nich.view.restaurant.RestaurantActivity
import com.example.natta.nich.view.scanqrcode.ScanQRCodeActivity
import com.example.natta.nich.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import uk.co.markormesher.android_fab.SpeedDialMenuAdapter
import uk.co.markormesher.android_fab.SpeedDialMenuItem

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var editor: SharedPreferences.Editor? = null
    private var sp: SharedPreferences? = null
    private val FROM_RESTAURANT = "FROM_RESTAURANT"
    private var latitude = 0.0
    private var longitude = 0.0
    private var bundle = Bundle()
    private lateinit var model: MainViewModel
    private val tab = object : TabLayout.OnTabSelectedListener {
        override fun onTabReselected(tab: TabLayout.Tab?) {

        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {
            val color = ContextCompat.getColor(applicationContext, R.color.colorTab)
            tab!!.icon!!.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        }

        override fun onTabSelected(tab: TabLayout.Tab?) {
            val color = ContextCompat.getColor(applicationContext, R.color.colorTabSelected)
            tab!!.icon!!.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        }

    }
    private val item = arrayOf("ทานที่บ้าน", "ทานที่ร้าน")
    private val speedDialMenuAdapter = object : SpeedDialMenuAdapter() {
        override fun getCount(): Int {
            return 2
        }

        override fun getMenuItem(context: Context, position: Int): SpeedDialMenuItem = when (position) {
            0 -> SpeedDialMenuItem(context, R.drawable.ic_back_home, item[position])
            1 -> SpeedDialMenuItem(context, R.drawable.ic_store, item[position])
            else -> throw IllegalArgumentException("No menu item: $position")
        }

        override fun onMenuItemClick(position: Int): Boolean {
            when (position) {
                0 -> {
                    editor!!.putInt(FROM_RESTAURANT, 0)
                    editor!!.commit()
                    startActivity(Intent(applicationContext, RestaurantActivity::class.java))
                }
                1 -> {
                    editor!!.putInt(FROM_RESTAURANT, 1)
                    editor!!.commit()
                    startActivity(Intent(applicationContext, ScanQRCodeActivity::class.java))

                }
            }
            return true
        }

        override fun fabRotationDegrees(): Float = 45f

    }
    private var mAuth = FirebaseAuth.getInstance()
    private var mAuthListener = FirebaseAuth.AuthStateListener {
        val user = it.currentUser
        if (user != null) {
//            successAnimation(Manifest.permission.CAMERA)
//            Delay(success_ani.duration).execute()


        } else {

            startActivity(Intent(applicationContext, LoginActivity::class.java))
            Log.d("MainActivity5555", "55555")
            finish()
        }
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
                            .withContext(this@MainActivity)
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

    override fun onStart() {
        super.onStart()
        sp = getSharedPreferences("MY_ORDER", 0)
        val firstOpen = sp!!.getBoolean("FIRSTOPENAPP", true)
        if (firstOpen) {
            startActivity(Intent(applicationContext, LoginActivity::class.java))
            Log.d("MainActivity5555", "55555555555555555555")
            finish()
        } else {
            mAuth.addAuthStateListener(mAuthListener)
            try {
                getLocation()
            } catch (e: SecurityException) {
            }
        }
        Log.d("MainActivity5555", firstOpen.toString())


    }

    override fun onStop() {
        super.onStop()
        if (mAuth != null) {
            mAuth.removeAuthStateListener(mAuthListener)
        }
    }

    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        editor = this.getSharedPreferences("MY_ORDER", Context.MODE_PRIVATE).edit()
//        val notification = NotificationCompat.Builder(this)
//            .setSmallIcon(R.drawable.restaurant_icon)
//            .setContentTitle("New Notification ")
//            .setContentText("555555")
//            .setAutoCancel(true)
//            .build()
//        val noti = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        noti.notify(100, notification)

        model = ViewModelProviders.of(this).get(MainViewModel::class.java)


        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        fabFavorite_RD.speedDialMenuAdapter = speedDialMenuAdapter
        fabFavorite_RD.contentCoverEnabled = false
        initMainTabPager()
        Delay(1000).execute()

    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun initMainTabPager() {
        val tabPager = MainTabPager(supportFragmentManager, bundle)
        viewPager.adapter = tabPager
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.getTabAt(0)!!.icon = resources.getDrawable(R.drawable.ic_home)
        tabLayout.getTabAt(1)!!.icon = resources.getDrawable(R.drawable.ic_order)
        tabLayout.getTabAt(2)!!.icon = resources.getDrawable(R.drawable.ic_notifications)
        tabLayout.getTabAt(0)!!.select()
        if (tabLayout.getTabAt(0)!!.isSelected) {
            val color = ContextCompat.getColor(applicationContext, R.color.colorTabSelected)
            tabLayout.getTabAt(0)!!.icon!!.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        }
        val color = ContextCompat.getColor(applicationContext, R.color.colorTab)
        tabLayout.getTabAt(1)!!.icon!!.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        tabLayout.getTabAt(2)!!.icon!!.setColorFilter(color, PorterDuff.Mode.SRC_IN)

        tabLayout.setOnTabSelectedListener(tab)

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_profile -> {
                startActivity(Intent(applicationContext, MyProfileActivity::class.java))
            }
            R.id.nav_order_history -> {
                startActivity(Intent(applicationContext, OrderHistoryActivity::class.java))
            }
            R.id.nav_favortie -> {
                startActivity(Intent(applicationContext, FavoriteActivity::class.java))
            }
            R.id.nav_random -> {
                startActivity(Intent(applicationContext, RandomFoodActivity::class.java))
            }
            R.id.nav_setting -> {

            }
            R.id.nav_about -> {
                startActivity(Intent(applicationContext, AboutApplicationActivity::class.java))
            }
            R.id.nav_osl -> {
                startActivity(Intent(applicationContext, OSLActivity::class.java))
            }
            R.id.nav_logout -> {
                val mAuth = FirebaseAuth.getInstance()
                if (mAuth.currentUser != null) {
                    mAuth.signOut()
//                    startActivity(Intent(applicationContext, LoginActivity::class.java))
//                    finish()
                }
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }


    @SuppressLint("MissingPermission")
    fun getLocation() {
        checkPermission()
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        val location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        val location2 = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)

        when {
            location != null -> {
                latitude = location.latitude
                longitude = location.longitude
                bundle.putDouble("latitude", latitude)
                bundle.putDouble("longitude", longitude)
//                Toast.makeText(this, "$longitude , $latitude", Toast.LENGTH_SHORT).show()


            }
            location1 != null -> {
                latitude = location1.latitude
                longitude = location1.longitude
                bundle.putDouble("latitude", latitude)
                bundle.putDouble("longitude", longitude)
//                Toast.makeText(this, "$longitude , $latitude", Toast.LENGTH_SHORT).show()


            }
            location2 != null -> {
                latitude = location2.latitude
                longitude = location2.longitude
                bundle.putDouble("latitude", latitude)
                bundle.putDouble("longitude", longitude)
//                Toast.makeText(this, "$longitude , $latitude", Toast.LENGTH_SHORT).show()

            }
            else -> Toast.makeText(this, "Unble to Trace your location", Toast.LENGTH_SHORT).show()
        }

    }

    internal inner class Delay(var time: Long) : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg p0: Void?): Void? {
            Thread.sleep(time)
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            getCustomer()
        }

    }

    private fun getCustomer() {
        model.getCustomer().observe(this, Observer {
            Log.d("Cus444", "${it?.firstName}")
//            name_profile_main.text = it?.firstName.toString()
            if (it != null) {
                updateUI(it, model.getEmail())
            }
        })

    }

    private fun updateUI(customer: Customer, email: String) {
        try {
            name_profile_main.text = customer.firstName.toString() + " " + customer.lastName.toString()
            email_profile_main.text = email

        } catch (e: IllegalStateException) {

        }
        try {
            Glide.with(this).load(customer.picture).apply(RequestOptions.errorOf(R.drawable.ic_error))
                .into(pic_profile_main)
        } catch (e: IllegalArgumentException) {
        }
    }
}
