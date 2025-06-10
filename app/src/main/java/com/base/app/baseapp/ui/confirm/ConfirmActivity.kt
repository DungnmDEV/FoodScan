package com.base.app.baseapp.ui.confirm

import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.base.app.baseapp.base.BaseActivity
import com.base.app.baseapp.databinding.ActivityConfirmBinding
import com.base.app.baseapp.ui.info.InfoActivity
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class ConfirmActivity : BaseActivity<ActivityConfirmBinding>(ActivityConfirmBinding::inflate) {

    private lateinit var auth: FirebaseAuth
    private lateinit var storedVerificationId: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun initView() {
        val name = intent.getStringExtra("name")?:""
        val email = intent.getStringExtra("email")?:""
        val phone = intent.getStringExtra("phone")?:""
        val pass = intent.getStringExtra("pass")?:""

        auth = FirebaseAuth.getInstance()

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d("OTP", "onVerificationCompleted:$credential")
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                if (e is FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(this@ConfirmActivity, "Số điện thoại không hợp lệ.", Toast.LENGTH_SHORT).show()
                } else if (e is FirebaseTooManyRequestsException) {
                    Toast.makeText(this@ConfirmActivity, "Quá nhiều yêu cầu. Vui lòng thử lại sau.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@ConfirmActivity, "Gửi OTP thất bại: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d("OTP", "onCodeSent:$verificationId")
                storedVerificationId = verificationId
                resendToken = token
            }
        }

        sendVerificationCode(formatPhoneNumber(phone))

        binding.otpView.setOtpCompletionListener {
            verifyPhoneNumberWithCode(it)
        }
    }

    private fun sendVerificationCode(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyPhoneNumberWithCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(storedVerificationId, code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user


                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(this, "Mã OTP không hợp lệ.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Xác minh thất bại: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
    }

    private fun formatPhoneNumber(phone: String): String {
        return when {
            phone.startsWith("+84") && phone.drop(3).startsWith("0") -> {
                "+84" + phone.drop(4)
            }
            phone.startsWith("0") -> {
                "+84" + phone.drop(1)
            }
            phone.startsWith("+84") -> {
                phone
            }
            else -> {
                phone
            }
        }
    }

}