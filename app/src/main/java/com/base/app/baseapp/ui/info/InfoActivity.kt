package com.base.app.baseapp.ui.info

import android.content.Intent
import com.base.app.baseapp.base.BaseActivity
import com.base.app.baseapp.databinding.ActivityInfoBinding
import com.base.app.baseapp.databinding.ActivityIntroBinding
import com.base.app.baseapp.ui.home.HomeActivity

class InfoActivity : BaseActivity<ActivityInfoBinding>(ActivityInfoBinding::inflate) {
    override fun initView() {
        binding.btnNext.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java).putExtra("name", binding.edtName.text.toString()))
        }
    }

}