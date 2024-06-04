package com.adv.ilook.view.ui.selectscreen

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.adv.ilook.databinding.FragmentSelectScreenBinding
import com.adv.ilook.databinding.FragmentSplashBinding
import com.adv.ilook.view.base.BaseFragment
import com.adv.ilook.view.ui.splash.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

private const val TAG = "SelectScreenFragment"
@AndroidEntryPoint
class SelectScreenFragment() :BaseFragment<FragmentSelectScreenBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSelectScreenBinding
        get() = FragmentSelectScreenBinding::inflate
    private var _viewBinding: FragmentSelectScreenBinding? = null
    val viewModel by viewModels<SelectScreenViewModel>()
    private var nextScreenId by Delegates.notNull<Int>()
    private var previousScreenId by Delegates.notNull<Int>()
    private val handler = Handler(Looper.getMainLooper())
    override fun setup(savedInstanceState: Bundle?) {
        Log.d(TAG, "setup: ")
        _viewBinding = binding
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.init { }
        }

    }

}