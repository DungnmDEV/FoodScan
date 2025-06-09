package com.base.app.baseapp.ui.intro

import android.content.Intent
import com.base.app.baseapp.R
import com.base.app.baseapp.base.BaseActivity
import com.base.app.baseapp.databinding.ActivityIntroBinding
import com.base.app.baseapp.model.IntroItem
import com.base.app.baseapp.ui.login.LoginActivity
import com.base.app.baseapp.ui.register.RegisterActivity

@Suppress("DEPRECATION")
class IntroActivity : BaseActivity<ActivityIntroBinding>(ActivityIntroBinding::inflate) {
    override fun initView() {
        val list = mutableListOf(
            IntroItem(R.drawable.img_intro_1, "Quét và tận hưởng!", "Ăn đúng - Sống khỏe - Ngăn biến chứng!"),
            IntroItem(
                R.drawable.img_intro_2,
                "Quét và tận hưởng!",
                "Chỉ một lần quét\n" + "Biết ngay món ăn có an toàn không!"
            ),
            IntroItem(
                R.drawable.img_intro_3, "Quét và tận hưởng!", "Không chỉ cảnh báo\n" +
                    "Chúng tôi còn gợi ý món thay thế an toàn!"
            )
        )

        binding.vp.adapter = IntroAdapter(list)

        binding.dotIndicator.attachTo(binding.vp)

        binding.btnNext.setOnClickListener {
            if (binding.vp.currentItem < list.size - 1) {
                binding.vp.currentItem++
            }else{
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }
    }

    override fun onBackPressed() {
        if(binding.vp.currentItem > 0){
            binding.vp.currentItem--
        }else{
            super.onBackPressed()
        }
    }
}