package com.example.natta.nich.viewmodel

import android.arch.lifecycle.ViewModel
import android.util.Patterns

class ForgotPasswordViewModel:ViewModel() {
    fun checkEmail(email: String): Boolean = email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
}