package com.base.app.baseapp.ui.login

import android.content.Intent
import android.widget.Toast
import com.base.app.baseapp.base.BaseActivity
import com.base.app.baseapp.database.AccountHelper
import com.base.app.baseapp.databinding.ActivityLoginBinding
import com.base.app.baseapp.ui.info.InfoActivity
import com.base.app.baseapp.ui.register.RegisterActivity
import com.base.app.baseapp.utils.Utils

class LoginActivity : BaseActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate) {

    override fun initView() {
        binding.btnNext.setOnClickListener {
            AccountHelper.login(
                binding.edtName.text.toString(),
                binding.edtPassword.text.toString()
            ) { success, msg, user ->
                if (success) {
                    Utils.currentUser = user
                    startActivity(Intent(this, InfoActivity::class.java))
                }else{
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

}