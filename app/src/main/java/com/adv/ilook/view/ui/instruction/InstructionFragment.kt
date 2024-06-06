package com.adv.ilook.view.ui.instruction

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.adv.ilook.R
import com.adv.ilook.databinding.FragmentInstructionBinding
import com.adv.ilook.databinding.FragmentSelectScreenBinding
import com.adv.ilook.view.base.BaseFragment

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.properties.Delegates
private const val TAG = "InstructionFragment"
@AndroidEntryPoint
class InstructionFragment() : BaseFragment<FragmentInstructionBinding>() {
    override var nextScreenId_1 by Delegates.notNull<Int>()
    override var nextScreenId_2 by Delegates.notNull<Int>()
    override var previousScreenId by Delegates.notNull<Int>()

    companion object {
        fun newInstance() = InstructionFragment()
    }
    private val viewModel: InstructionViewModel by viewModels()
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentInstructionBinding
        get() = FragmentInstructionBinding::inflate
    private var _viewBinding: FragmentInstructionBinding? = null

    override fun setup(savedInstanceState: Bundle?) {
        Log.d(TAG, "setup: ")
        _viewBinding = binding
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.init { }}
        viewModel.nextScreenLiveData.observe(this@InstructionFragment){
            nextScreenId_1 = it
        }

    }

}