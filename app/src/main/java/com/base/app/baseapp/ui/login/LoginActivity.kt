package com.base.app.baseapp.ui.login

import android.content.Intent
import com.base.app.baseapp.base.BaseActivity
import com.base.app.baseapp.databinding.ActivityLoginBinding
import com.base.app.baseapp.ui.info.InfoActivity
import com.base.app.baseapp.ui.register.RegisterActivity

class LoginActivity : BaseActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate) {
    override fun initView() {
        binding.btnNext.setOnClickListener {
            startActivity(Intent(this, InfoActivity::class.java))
        }

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}