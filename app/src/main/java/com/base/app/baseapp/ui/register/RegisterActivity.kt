package com.base.app.baseapp.ui.register

import android.content.Intent
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.base.app.baseapp.R
import com.base.app.baseapp.base.BaseActivity
import com.base.app.baseapp.data.RegisterHelper
import com.base.app.baseapp.databinding.ActivityRegisterBinding
import com.base.app.baseapp.ui.confirm.ConfirmActivity
import com.base.app.baseapp.ui.home.HomeActivity
import com.base.app.baseapp.ui.info.InfoActivity
import com.base.app.baseapp.ui.login.LoginActivity
import com.base.app.baseapp.utils.Utils.invisible
import com.base.app.baseapp.utils.Utils.visible

class RegisterActivity : BaseActivity<ActivityRegisterBinding>(ActivityRegisterBinding::inflate) {

    private var isChecked = false

    override fun initView() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnCheck.setOnClickListener {
            isChecked = !isChecked
            binding.icCheck.setImageResource(
                if (isChecked) R.drawable.ic_check_true else R.drawable.ic_check_false
            )
        }

        binding.btnLogin.setOnClickListener {
            finish()
        }

        binding.btnNext.setOnClickListener {
            if (validate()) {
                RegisterHelper.registerUser(
                    context = this,
                    name = binding.edtName.text.toString().trim(),
                    email = binding.edtEmail.text.toString().trim(),
                    phone = binding.edtPhoneNumber.text.toString().trim(),
                    password = binding.edtPassword.text.toString(),
                    onSuccess = {
                        val intent = Intent(this, InfoActivity::class.java)
                        startActivity(intent)
                    },
                    onFailure = { error ->
                        Toast.makeText(this, error, Toast.LENGTH_LONG).show()
                    }
                )

            }
            if (validate()) {
                startActivity(Intent(this, ConfirmActivity::class.java).putExtra("phone_number", binding.edtPhoneNumber.text.toString()))
            }
        }
    }

    private fun validate(): Boolean {
        var isValid = true

        // Validate Name
        val name = binding.edtName.text.toString().trim()
        if (name.length < 3) {
            binding.lnErrorName.visible()
            binding.tvErrorName.text = "Tên không hợp lệ (ít nhất 3 ký tự)"
            isValid = false
        } else {
            binding.lnErrorName.invisible()
        }

        // Validate Email
        val email = binding.edtEmail.text.toString().trim()
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.lnErrorMail.visible()
            binding.tvErrorMail.text = "Email không hợp lệ"
            isValid = false
        } else {
            binding.lnErrorMail.invisible()
        }

        // Validate Phone Number
        val phone = binding.edtPhoneNumber.text.toString().trim()
        if (phone.length < 9 || phone.length > 11 || !phone.all { it.isDigit() }) {
            binding.lnPhoneNumber.visible()
            binding.tvErrorPhoneNumber.text = "Số điện thoại không hợp lệ"
            isValid = false
        } else {
            binding.lnPhoneNumber.invisible()
        }

        // Validate Password
        val password = binding.edtPassword.text.toString()
        if (password.length < 6) {
            binding.tvErrorPass.visible()
            binding.tvErrorPass.text = "Mật khẩu phải có ít nhất 6 ký tự"
            isValid = false
        } else {
            binding.tvErrorPass.invisible()
        }

        // Validate Agreement
        if (!isChecked) {
            Toast.makeText(this, "Vui lòng đồng ý với điều khoản", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        return isValid
    }

}
