package com.adv.ilook.view.ui.instruction

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.adv.ilook.R
import com.adv.ilook.databinding.FragmentInstructionBinding
import com.adv.ilook.databinding.FragmentSelectScreenBinding
import com.adv.ilook.view.base.BaseFragment

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.newCoroutineContext
import kotlin.coroutines.coroutineContext
import kotlin.properties.Delegates

private const val TAG = "InstructionFragment"

@AndroidEntryPoint
class InstructionFragment() : BaseFragment<FragmentInstructionBinding>() {
    override var nextScreenId_1 by Delegates.notNull<Int>()
    override var nextScreenId_2 by Delegates.notNull<Int>()
    override var previousScreenId by Delegates.notNull<Int>()

    // Create an OnBackPressedCallback to handle the back button event
    private val onBackPress = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            isEnabled = true
            findNavControl()?.run {
                when (currentDestination?.id) {
                    R.id.instructionFragment -> {
                        Toast.makeText(
                            requireActivity(),
                            "instruction fragment",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        nav(previousScreenId)
                    }

                    else -> {
                        requireActivity().finish()
                    }

                }
            }
        }
    }

    companion object {
        fun newInstance() = InstructionFragment()
    }

    private val viewModel: InstructionViewModel by viewModels()
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentInstructionBinding
        get() = FragmentInstructionBinding::inflate
    private var viewBinding: FragmentInstructionBinding? = null

    override fun setup(savedInstanceState: Bundle?) {
        Log.d(TAG, "setup: ")
        viewBinding = binding
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPress)
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.init { }
        }
        viewModel.nextScreenLiveData_1.observe(this@InstructionFragment) {
            nextScreenId_1 = it
        }
        viewModel.prevScreenLiveData.observe(this@InstructionFragment) {
            previousScreenId = it
        }

        liveDataObserver()

    }

    fun liveDataObserver() {
        viewBinding?.apply {
            viewModel.tv_select_screen_header.observe(this@InstructionFragment) {
                headerTitle.text = it
            }
            viewModel.html_terms_of_use_enable.observe(this@InstructionFragment) { enable ->
                if (enable) {
                    viewModel.html_terms_of_use.observe(this@InstructionFragment) { htmlStr ->
                        webViewSetUp(enable, htmlStr.trimIndent())
                    }
                } else {
                    webViewSetUp(enable)
                }
            }

            viewModel.btn_agree_text.observe(this@InstructionFragment) { text ->
                agreeButton.text = text
            }
            viewModel.btn_disagree_text.observe(this@InstructionFragment) { text ->
                disagreeButton.text = text
            }
            viewModel.btn_agree_enable.observe(this@InstructionFragment) { enable ->
                agreeButton.isEnabled = enable
            }
            viewModel.btn_disagree_enable.observe(this@InstructionFragment) { enable ->
                disagreeButton.isEnabled = enable
            }
        }
    }

    fun webViewSetUp(enable: Boolean = false, htmlStr: String = "app/src/main/assets/terms.html") {
        viewBinding?.apply {

            webView.settings.javaScriptEnabled = false
            webView.webViewClient = WebViewClient()
            if (enable)
                webView.loadData(htmlStr, "text/html", "UTF-8")
            else
                webView.loadUrl(htmlStr)
        }
    }

    override fun onResume() {
        super.onResume()
        uiReactiveAction()
    }

    private fun uiReactiveAction() {
        Log.d(TAG, "uiReactiveAction: ")
        viewBinding?.apply {
            agreeButton.setOnClickListener {
                nav(nextScreenId_1)
            }
            disagreeButton.setOnClickListener {
                nav(previousScreenId)
            }
        }
    }
}