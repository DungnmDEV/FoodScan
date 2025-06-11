package com.base.app.baseapp.ui.home

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.base.app.baseapp.base.BaseActivity
import com.base.app.baseapp.databinding.ActivityHomeBinding

class HomeActivity : BaseActivity<ActivityHomeBinding>(ActivityHomeBinding::inflate) {
    override fun initView() {
        val name = intent.getStringExtra("name") ?: "No name"

        binding.vp.adapter = ViewPagerAdapter(this)

        binding.vp.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                selectTab(position)
            }
        })

        binding.btnHome.setOnClickListener {
            binding.vp.currentItem = 0
        }
        binding.btnScan.setOnClickListener {
            binding.vp.currentItem = 1
        }
        binding.btnHistory.setOnClickListener {
            binding.vp.currentItem = 2
        }
        binding.btnInfo.setOnClickListener {
            binding.vp.currentItem = 3
        }

    }

    fun selectScan(){
        binding.vp.currentItem = 1
    }

    fun selectHistory(){
        binding.vp.currentItem = 2
    }

    inner class ViewPagerAdapter(fragmentActivity: FragmentActivity) :
        FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int = 4

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                1 -> ScanFragment()
                2 -> HistoryFragment()
                3 -> InfoFragment()
                else -> HomeFragment()
            }
        }
    }

    private fun selectTab(position: Int){
        binding.btnHome.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#00000000"))
        binding.btnScan.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#00000000"))
        binding.btnHistory.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#00000000"))
        binding.btnInfo.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#00000000"))

        when(position){
            1 -> binding.btnScan.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D0EDFB"))
            2 -> binding.btnHistory.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D0EDFB"))
            3 -> binding.btnInfo.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D0EDFB"))
            else -> binding.btnHome.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D0EDFB"))
        }
    }
}