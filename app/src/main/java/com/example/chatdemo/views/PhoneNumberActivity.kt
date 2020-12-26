package com.example.chatdemo.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.chatdemo.R
import com.example.chatdemo.utils.showToast
import com.google.firebase.auth.FirebaseAuth
import com.mukesh.OtpView
import kotlinx.android.synthetic.main.activity_phone_number.*

class PhoneNumberActivity : AppCompatActivity() {

    val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_number)

        if(firebaseAuth.currentUser!=null){
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

        supportActionBar?.hide()
        etPhone.requestFocus()
        btnContinue.setOnClickListener {
            if(etPhone.text.toString().length<10){
                 showToast("Number is invalid")
            }else{
                val intent=Intent(this,OTPActivity::class.java).apply {
                    putExtra("phoneNumber","+91${etPhone.text}")
                    startActivity(this)
                }
            }
        }
    }
}