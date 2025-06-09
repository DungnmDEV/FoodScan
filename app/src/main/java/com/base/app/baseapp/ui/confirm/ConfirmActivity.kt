package com.base.app.baseapp.ui.confirm

import android.content.Intent
import android.util.Log
import com.base.app.baseapp.base.BaseActivity
import com.base.app.baseapp.databinding.ActivityConfirmBinding
import com.base.app.baseapp.ui.info.InfoActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class ConfirmActivity : BaseActivity<ActivityConfirmBinding>(ActivityConfirmBinding::inflate) {
    override fun initView() {

        val phoneNumber = intent.getStringExtra("phone_number")
        val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
            .setPhoneNumber("+84398880698")       // số điện thoại (bắt buộc có mã quốc gia)
            .setTimeout(60L, TimeUnit.SECONDS)    // thời gian timeout
            .setActivity(this)                    // activity hiện tại
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // Nếu tự động xác minh thành công
//                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Log.e("OTP", "Gửi OTP thất bại: ${e.message}")
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {

                    Log.d("OTP", "Mã OTP đã gửi")
                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)

        binding.otpView.setOtpCompletionListener {
            startActivity(Intent(this, InfoActivity::class.java))
        }
    }
}