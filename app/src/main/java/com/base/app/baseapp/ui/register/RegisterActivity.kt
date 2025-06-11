package com.base.app.baseapp.ui.register

import android.content.Intent
import android.util.Patterns
import android.widget.Toast
import com.base.app.baseapp.R
import com.base.app.baseapp.base.BaseActivity
import com.base.app.baseapp.database.AccountHelper
import com.base.app.baseapp.databinding.ActivityRegisterBinding
import com.base.app.baseapp.ui.confirm.ConfirmActivity
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
                startActivity(
                    Intent(this, ConfirmActivity::class.java)
                        .putExtra("name", binding.edtName.text.toString())
                        .putExtra("email", binding.edtEmail.text.toString())
                        .putExtra("phone", binding.edtPhoneNumber.text.toString())
                        .putExtra("pass", binding.edtPassword.text.toString())
                )
            }
        }
    }

    private fun validate(): Boolean {
        var isValid = true

        val name = binding.edtName.text.toString().trim()
        if (name.length < 3) {
            binding.lnErrorName.visible()
            binding.tvErrorName.text = "Tên không hợp lệ (ít nhất 3 ký tự)"
            isValid = false
        } else {
            binding.lnErrorName.invisible()
        }

        val email = binding.edtEmail.text.toString().trim()
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.lnErrorMail.visible()
            binding.tvErrorMail.text = "Email không hợp lệ"
            isValid = false
        } else {
            AccountHelper.isEmailUsed(email){
                if (it){
                    binding.lnErrorMail.visible()
                    binding.tvErrorMail.text = "Email đã được sử dụng"
                    isValid = false
                }else{
                    binding.lnErrorMail.invisible()
                }
            }
        }

        val phone = binding.edtPhoneNumber.text.toString().trim()
        if (phone.length < 9 || phone.length > 11 || !phone.all { it.isDigit() }) {
            binding.lnPhoneNumber.visible()
            binding.tvErrorPhoneNumber.text = "Số điện thoại không hợp lệ"
            isValid = false
        } else {
            AccountHelper.isPhoneUsed(phone){
                if (it){
                    binding.lnPhoneNumber.visible()
                    binding.tvErrorPhoneNumber.text = "Số điện thoại đã được sử dụng"
                    isValid = false
                }else{
                    binding.lnPhoneNumber.invisible()
                }
            }
        }



        val password = binding.edtPassword.text.toString()
        if (password.length < 6) {
            binding.tvErrorPass.visible()
            binding.tvErrorPass.text = "Mật khẩu phải có ít nhất 6 ký tự"
            isValid = false
        } else {
            binding.tvErrorPass.invisible()
        }

        if (!isChecked) {
            Toast.makeText(this, "Vui lòng đồng ý với điều khoản", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        return isValid
    }

}
