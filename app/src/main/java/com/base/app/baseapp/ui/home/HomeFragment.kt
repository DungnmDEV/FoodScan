package com.base.app.baseapp.ui.home

import com.base.app.baseapp.base.BaseFragment
import com.base.app.baseapp.databinding.FragmentHomeBinding
import com.base.app.baseapp.utils.Utils

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {
    override fun initView() {
        binding.tvName.text = Utils.currentUser?.name
        binding.btnScan.setOnClickListener {
            (requireActivity() as HomeActivity).selectScan()
        }
        binding.btnHistory.setOnClickListener {
            (requireActivity() as HomeActivity).selectHistory()
        }
    }
}