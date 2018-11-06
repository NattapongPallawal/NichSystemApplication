package com.example.natta.nich.view.register

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import com.example.natta.nich.R
import kotlinx.android.synthetic.main.activity_register1.*

class Register1Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register1)
        register1_next.setOnClickListener {
            val email = register1_email.text.toString().trim()
            val password = register1_password.text.toString().trim()
            val confirmPassword = register1_confirmPassword.text.toString()

            if (email.isEmpty())  register1_email.error = "กรุณาป้อน email"
            if (password.isEmpty())  register1_password.error = "กรุณาป้อนรหัสผ่าน"
            if (confirmPassword.isEmpty())  register1_confirmPassword.error = "กรุณาป้อนยืนยันรหัสผ่าน"

            if (checkEmail(email)) {
                if (checkPassword(password)) {
                    if (password == confirmPassword) {
                        val i = Intent(applicationContext, Register2Activity::class.java)
                        i.putExtra("email", email)
                        i.putExtra("password", password)
                        startActivityForResult(i, 1)
                    } else {
                        register1_confirmPassword.error = "รหัสผ่านไม่ตรงกัน"
                    }
                } else {
                    register1_password.error = "รหัสต้องมากกว่า 6 ตัว"
                }
            } else {
                register1_email.error = "email ไม่ถูกต้อง"
            }


        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == 1) {
                setResult(2, data)
                finish()
            }
        }
    }

    private fun checkEmail(email: String): Boolean = email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    private fun checkPassword(password: String): Boolean {
        return password.isNotEmpty() && password.length >= 6
    }
}
