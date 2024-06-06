package com.adv.ilook.view.ui.selectscreen

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.adv.ilook.R
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
    override var nextScreenId_1 by Delegates.notNull<Int>()
    override var nextScreenId_2 by Delegates.notNull<Int>()
    override var previousScreenId by Delegates.notNull<Int>()
    private val handler = Handler(Looper.getMainLooper())
    override fun setup(savedInstanceState: Bundle?) {
        Log.d(TAG, "setup: ")
        viewBinding = binding
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
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

        liveDataObserver()
    }

    private fun liveDataObserver() {
        viewBinding?.apply {
            viewModel.tv_select_screen_header.observe(viewLifecycleOwner) {
                headerTitle.text = it!!
            }
            viewModel.btn_guide_text.observe(viewLifecycleOwner) {
                txtGuideMode.text = it!!
            }
            viewModel.btn_vi_text.observe(viewLifecycleOwner) {
                txtVisuallyImpairedMode.text = it!!
            }
        }

    }

    fun uiReactiveAction() {
        viewBinding?.apply {
            txtGuideMode.setOnClickListener {
                nav(nextScreenId_1)
            }
            txtVisuallyImpairedMode.setOnClickListener {
                nav(nextScreenId_2)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        uiReactiveAction()
    }
    // Create an OnBackPressedCallback to handle the back button event
    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            isEnabled = true
            findNavControl()?.run {
                when (currentDestination?.id) {
                    R.id.selectScreenFragment -> {
                        Toast.makeText(requireActivity(), "splash fragment", Toast.LENGTH_SHORT)
                            .show()
                        if (previousScreenId==-1){
                            requireActivity().finish()
                        }else{
                            nav(previousScreenId)
                        }

                    }

                    else -> {
                        Toast.makeText(requireActivity(), "invalid", Toast.LENGTH_SHORT)
                            .show()
                    }

                }
            }
        }
    }
}