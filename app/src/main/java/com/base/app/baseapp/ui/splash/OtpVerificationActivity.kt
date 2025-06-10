package com.base.app.baseapp.ui.splash

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.base.app.baseapp.R
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class OtpVerificationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var storedVerificationId: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    private lateinit var editTextPhoneNumber: EditText
    private lateinit var buttonSendOtp: Button
    private lateinit var editTextOtpCode: EditText
    private lateinit var buttonVerifyOtp: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_verification) // Đảm bảo bạn có layout này

        auth = FirebaseAuth.getInstance()

        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber)
        buttonSendOtp = findViewById(R.id.buttonSendOtp)
        editTextOtpCode = findViewById(R.id.editTextOtpCode)
        buttonVerifyOtp = findViewById(R.id.buttonVerifyOtp)

        // Khởi tạo Callbacks
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // Tự động xác minh: nếu mã OTP được gửi tự động và xác minh
                // Người dùng có thể đã được xác minh nếu Android nhận được SMS
                Log.d("OTP", "onVerificationCompleted:$credential")
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.w("OTP", "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Định dạng số điện thoại không hợp lệ
                    Toast.makeText(this@OtpVerificationActivity, "Số điện thoại không hợp lệ.", Toast.LENGTH_SHORT).show()
                } else if (e is FirebaseTooManyRequestsException) {
                    // Quá nhiều yêu cầu SMS, bị chặn để ngăn chặn lạm dụng
                    Toast.makeText(this@OtpVerificationActivity, "Quá nhiều yêu cầu. Vui lòng thử lại sau.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@OtpVerificationActivity, "Gửi OTP thất bại: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // Mã OTP đã được gửi đến số điện thoại được cung cấp.
                // Lưu verificationId và resendToken để sử dụng sau này nếu cần xác minh thủ công.
                Log.d("OTP", "onCodeSent:$verificationId")
                storedVerificationId = verificationId
                resendToken = token
                Toast.makeText(this@OtpVerificationActivity, "Mã OTP đã được gửi.", Toast.LENGTH_LONG).show()
                // Hiển thị giao diện nhập mã OTP
                editTextOtpCode.visibility = android.view.View.VISIBLE
                buttonVerifyOtp.visibility = android.view.View.VISIBLE
                buttonSendOtp.text = "Gửi lại OTP" // Tùy chọn: đổi text button
            }
        }

        buttonSendOtp.setOnClickListener {
            val phoneNumber = editTextPhoneNumber.text.toString().trim()
            if (phoneNumber.isEmpty()) {
                editTextPhoneNumber.error = "Vui lòng nhập số điện thoại"
                editTextPhoneNumber.requestFocus()
                return@setOnClickListener
            }
            // Thêm tiền tố quốc gia nếu chưa có (ví dụ +84 cho Việt Nam)
            val fullPhoneNumber = if (!phoneNumber.startsWith("+")) "+84$phoneNumber" else phoneNumber

            sendVerificationCode(fullPhoneNumber)
        }

        buttonVerifyOtp.setOnClickListener {
            val otpCode = editTextOtpCode.text.toString().trim()
            if (otpCode.isEmpty()) {
                editTextOtpCode.error = "Vui lòng nhập mã OTP"
                editTextOtpCode.requestFocus()
                return@setOnClickListener
            }
            verifyPhoneNumberWithCode(otpCode)
        }
    }

    private fun sendVerificationCode(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Số điện thoại để xác minh
            .setTimeout(60L, TimeUnit.SECONDS) // Thời gian chờ xác minh (60 giây)
            .setActivity(this)                 // Activity hiện tại
            .setCallbacks(callbacks)           // Callbacks xử lý các sự kiện
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        Toast.makeText(this, "Đang gửi OTP...", Toast.LENGTH_SHORT).show()
    }

    private fun verifyPhoneNumberWithCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(storedVerificationId, code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Đăng nhập thành công, người dùng đã được xác thực
                    val user = task.result?.user
                    Toast.makeText(this, "Xác minh thành công! User ID: ${user?.uid}", Toast.LENGTH_LONG).show()
                    // Chuyển sang Activity tiếp theo hoặc hiển thị nội dung chính
                } else {
                    // Đăng nhập thất bại
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // Mã OTP không hợp lệ
                        Toast.makeText(this, "Mã OTP không hợp lệ.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Xác minh thất bại: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
    }
}