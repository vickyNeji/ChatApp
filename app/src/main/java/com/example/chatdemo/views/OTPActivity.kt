package com.example.chatdemo.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.chatdemo.R
import com.example.chatdemo.utils.showToast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.mukesh.OnOtpCompletionListener
import kotlinx.android.synthetic.main.activity_o_t_p.*
import java.util.concurrent.TimeUnit

class OTPActivity : AppCompatActivity() {

    lateinit var firebaseAuth:FirebaseAuth
    var sentCode=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_o_t_p)

        otp_view.requestFocus()

        firebaseAuth= FirebaseAuth.getInstance()

       val number=intent.getStringExtra("phoneNumber")
        textView.text="Verify $number"

        supportActionBar?.hide()

        val phoneAuthOptions=PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(number)
            .setTimeout(60L,TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(p0: PhoneAuthCredential) {

                }

                override fun onVerificationFailed(p0: FirebaseException) {

                }

                override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(p0, p1)
                    sentCode=p0
                }
            }).build()

            PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions)

            otp_view.setOtpCompletionListener(OnOtpCompletionListener {otp->

              val credential:PhoneAuthCredential=PhoneAuthProvider.getCredential(sentCode,otp)
                firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task->
                    if(task.isSuccessful){
                        showToast("Successfully verified")
                        startActivity(Intent(this,SetupProfileActivity::class.java))
                    }else{
                        showToast("Wrong otp ")

                    }
                }

            })

    }
}