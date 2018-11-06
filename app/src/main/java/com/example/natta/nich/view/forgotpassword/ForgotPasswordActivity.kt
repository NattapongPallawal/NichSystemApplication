package com.example.natta.nich.view.forgotpassword

import android.arch.lifecycle.ViewModelProviders
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.natta.nich.R
import com.example.natta.nich.viewmodel.ForgotPasswordViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPasswordActivity : AppCompatActivity() {
    private var mAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        val model = ViewModelProviders.of(this).get(ForgotPasswordViewModel::class.java)

        button_send_email.setOnClickListener {
            val email = forgot_email.text.toString().trim()
            if (model.checkEmail(email)) {
                forgot_ani.setAnimation("loading.json")
                forgot_ani.loop(true)
                forgot_ani.playAnimation()
                mAuth.sendPasswordResetEmail(email).addOnCompleteListener {
                    if (it.isSuccessful) {
                        forgot_ani.pauseAnimation()
                        forgot_ani.setAnimation("success.json")
                        forgot_ani.loop(false)
                        forgot_ani.playAnimation()
                        Delay(forgot_ani.duration).execute()
                    } else {
                        forgot_ani.pauseAnimation()
                        forgot_ani.setAnimation("error.json")
                        forgot_ani.loop(false)
                        forgot_ani.playAnimation()
                    }
                }
            } else {
                forgot_ani.setAnimation("error.json")
                forgot_ani.playAnimation()
            }
        }
    }
    internal inner class Delay(var duration: Long) : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg p0: Void?): Void? {
            Thread.sleep(duration)
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            finish()
        }

    }
}
