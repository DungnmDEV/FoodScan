package com.base.app.baseapp.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.base.app.baseapp.utils.Constants
import com.base.app.baseapp.utils.PreferenceHelper

abstract class BaseFragment<VB : ViewBinding>(private val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB) :
    Fragment() {

    private lateinit var _binding: VB
    protected val binding get() = _binding
    protected lateinit var preferenceHelper: PreferenceHelper
    protected val TAG ="TAG123"

    protected abstract fun initView()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bindingInflater.invoke(inflater, container, false)
        preferenceHelper = PreferenceHelper(
            requireContext().getSharedPreferences(
                Constants.SHARE_PREFERENCE_NAME,
                MODE_PRIVATE
            )
        )
        initView()
        return binding.root
    }
}
