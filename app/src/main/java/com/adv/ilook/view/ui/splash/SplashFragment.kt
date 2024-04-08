package com.adv.ilook.view.ui.splash

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.adv.ilook.R
import com.adv.ilook.databinding.FragmentSplashBinding
import com.adv.ilook.view.base.BaseFragment

class SplashFragment : BaseFragment<FragmentSplashBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSplashBinding
        get() = FragmentSplashBinding::inflate
    private var _viewBinding: FragmentSplashBinding? = null
    override fun setup(savedInstanceState: Bundle?) {
        _viewBinding = binding
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }
}