package com.example.natta.nich.view.aboutapplication

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.natta.nich.R

class AboutApplicationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_application)
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
       // actionBar.setBackgroundDrawable(ColorDrawable(resources.getString(R.drawable.bg_toolbar)))
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}
