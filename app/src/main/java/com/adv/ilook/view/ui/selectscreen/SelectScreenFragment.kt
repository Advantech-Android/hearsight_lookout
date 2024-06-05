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
import com.adv.ilook.view.ui.splash.TypeOfData
import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

private const val TAG = "SelectScreenFragment"

@AndroidEntryPoint
class SelectScreenFragment() : BaseFragment<FragmentSelectScreenBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSelectScreenBinding
        get() = FragmentSelectScreenBinding::inflate
    private var viewBinding: FragmentSelectScreenBinding? = null
    val viewModel by viewModels<SelectScreenViewModel>()
    private var nextScreenId_1 by Delegates.notNull<Int>()
    private var nextScreenId_2 by Delegates.notNull<Int>()
    private var previousScreenId by Delegates.notNull<Int>()
    private val handler = Handler(Looper.getMainLooper())
    override fun setup(savedInstanceState: Bundle?) {
        Log.d(TAG, "setup: ")
        viewBinding = binding
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.init { }
        }
        viewModel.nextScreenLiveData_1.observe(this@SelectScreenFragment) {
            nextScreenId_1 = it
        }
        viewModel.nextScreenLiveData_2.observe(this@SelectScreenFragment) {
            nextScreenId_2 = it
        }
        viewModel.prevScreenLiveData.observe(this@SelectScreenFragment) {
            previousScreenId = it
        }
        uiSetUp()
    }

    fun uiSetUp() {
        viewBinding?.apply {
            guideMode.setOnClickListener {
                nav(nextScreenId_1)
            }
            helpSeekerMode.setOnClickListener {
                nav(nextScreenId_2)
            }
        }
    }

    override fun onResume() {
        super.onResume()

    }

}