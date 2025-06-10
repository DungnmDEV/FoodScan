package com.base.app.baseapp.ui.info

import android.R
import android.content.Intent
import android.widget.ArrayAdapter
import com.base.app.baseapp.base.BaseActivity
import com.base.app.baseapp.databinding.ActivityInfoBinding
import com.base.app.baseapp.databinding.ActivityIntroBinding
import com.base.app.baseapp.ui.home.HomeActivity

class InfoActivity : BaseActivity<ActivityInfoBinding>(ActivityInfoBinding::inflate) {
    override fun initView() {
        binding.btnNext.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        val genderOptions = listOf("Nam", "Nữ")

        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, genderOptions)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spinner1.adapter = adapter

        val genderOptions2 = listOf("Không", "Tiền tiểu đường", "Tiểu đường loại 1", "Tiểu đường loại 2", "Biến chứng thận")

        val adapter2 = ArrayAdapter(this, R.layout.simple_spinner_item, genderOptions2)
        adapter2.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spinner2.adapter = adapter2
    }

}