package com.base.app.baseapp.ui.confirm

import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import com.base.app.baseapp.base.BaseActivity
import com.base.app.baseapp.databinding.ActivityConfirmBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class ConfirmActivity : BaseActivity<ActivityConfirmBinding>(ActivityConfirmBinding::inflate) {

    private lateinit var auth: FirebaseAuth
    private lateinit var storedVerificationId: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var countdownTimer: CountDownTimer
    private var countdownRunning = false
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun initView() {
        val name = intent.getStringExtra("name") ?: ""
        val email = intent.getStringExtra("email") ?: ""
        val phone = intent.getStringExtra("phone") ?: ""
        val pass = intent.getStringExtra("pass") ?: ""

        auth = FirebaseAuth.getInstance()

        initCallbacks()
        sendVerificationCode(formatPhoneNumber(phone))
        initListeners()
        startCountdown()
    }

    private fun initListeners() {
        binding.otpView.setOtpCompletionListener {
            verifyPhoneNumberWithCode(it)
        }

        binding.btnAgain.setOnClickListener {
            if (!countdownRunning) {
                startCountdown()
                sendVerificationCode(formatPhoneNumber(intent.getStringExtra("phone") ?: ""))
            }
        }
    }

    private fun initCallbacks() {
        val activity = this
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d("OTP", "onVerificationCompleted:$credential")
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                val message = when (e) {
                    is FirebaseAuthInvalidCredentialsException -> "Số điện thoại không hợp lệ."
                    is FirebaseTooManyRequestsException -> "Quá nhiều yêu cầu. Vui lòng thử lại sau."
                    else -> "Gửi OTP thất bại: ${e.message}"
                }
                Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
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
    }

    private fun startCountdown() {
        countdownTimer = object : CountDownTimer(2 * 60 * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / 60000
                val seconds = (millisUntilFinished % 60000) / 1000
                binding.tvTime.text = "Mã OTP có hiệu lực trong vòng %02d:%02d".format(minutes, seconds)
                countdownRunning = true
            }

            override fun onFinish() {
                binding.tvTime.text = "Mã OTP đã hết hiệu lực"
                countdownRunning = false
            }
        }
        countdownTimer.start()
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
                    // TODO: handle success logic here
                } else {
                    val message = if (task.exception is FirebaseAuthInvalidCredentialsException)
                        "Mã OTP không hợp lệ." else
                        "Xác minh thất bại: ${task.exception?.message}"
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun formatPhoneNumber(phone: String): String {
        return when {
            phone.startsWith("+84") && phone.drop(3).startsWith("0") -> "+84${phone.drop(4)}"
            phone.startsWith("0") -> "+84${phone.drop(1)}"
            phone.startsWith("+84") -> phone
            else -> phone
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (countdownRunning) {
            countdownTimer.cancel()
        }
    }

    override fun onPause() {
        super.onPause()
        if (countdownRunning) {
            countdownTimer.cancel()
        }
    }

    override fun onResume() {
        super.onResume()
        if (countdownRunning) {
            startCountdown()
        }
    }
}
