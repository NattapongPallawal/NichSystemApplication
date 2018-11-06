package com.example.natta.nich.view.register

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.natta.nich.R
import kotlinx.android.synthetic.main.activity_register2.*


class Register2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register2)
        val email = intent.getStringExtra("email")
        val password = intent.getStringExtra("password")
        register2_button.setOnClickListener {
            val firstName = register2_firstName.text.toString().trim()
            val lastName = register2_lastName.text.toString().trim()
            val phoneNumber = register2_phoneNumber.text.toString().trim()

            if (firstName.isEmpty()) register2_firstName.error = "กรุณาป้อนชื่อ"
            if (lastName.isEmpty()) register2_lastName.error = "กรุณาป้อนนามสกุล"
            if (phoneNumber.isEmpty()) register2_phoneNumber.error = "กรุณาป้อนเบอร์โทรศัพท์"

            if (checkValue(firstName, lastName, phoneNumber)) {

                val reg = arrayListOf<String>(firstName, lastName, phoneNumber, email, password)
                val i = Intent()
                i.putExtra("reg", reg)
                setResult(1, i)
                finish()
            }
        }
    }

    private fun checkValue(firstName: String, lastName: String, phoneNumber: String): Boolean {
        return if (!(firstName.isEmpty() || lastName.isEmpty() || phoneNumber.isEmpty())) {
            if (phoneNumber[0] == '0' && phoneNumber.length == 10) {
                true
            } else {
                register2_phoneNumber.error = "หมายเลขโทรศัพท์ไม่ถูดต้อง"
                false
            }
        } else {
            false
        }
    }
}
