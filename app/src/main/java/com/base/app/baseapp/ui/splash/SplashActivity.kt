package com.base.app.baseapp.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import com.base.app.baseapp.base.BaseActivity
import com.base.app.baseapp.databinding.ActivitySplashBinding
import com.base.app.baseapp.ui.intro.IntroActivity
import com.base.app.baseapp.ui.login.LoginActivity
import com.base.app.baseapp.ui.register.RegisterActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate) {

    @SuppressLint("SetTextI18n")
    override fun initView() {
        Log.d(TAG, "initView: ")
        startActivity(Intent(this@SplashActivity, IntroActivity::class.java))
        finish()
        if (!isTaskRoot
            && intent.hasCategory(Intent.CATEGORY_LAUNCHER)
            && intent.action != null && intent.action.equals(Intent.ACTION_MAIN)
        ) {
            finish()
            return
        }

//        var i = 0
//        lifecycleScope.launch(Dispatchers.Main) {
//            while (i < 100) {
//                i++
//                binding.tvLoading.text = "${getString(R.string.loading)} (${i}%)"
//                binding.sb.progress = i
//                delay(25)
//            }
//            startActivity(Intent(this@SplashActivity, IntroActivity::class.java))
//            finish()
//        }
    }
}