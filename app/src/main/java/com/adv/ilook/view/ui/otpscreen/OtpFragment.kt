package com.adv.ilook.view.ui.otpscreen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.adv.ilook.R
import com.adv.ilook.databinding.FragmentLoginBinding
import com.adv.ilook.databinding.FragmentOtpBinding
import com.adv.ilook.databinding.FragmentSplashBinding
import com.adv.ilook.model.util.assets.BundleKeys.LOGIN_OTP_KEY
import com.adv.ilook.model.util.assets.BundleKeys.USER_NAME_KEY
import com.adv.ilook.model.util.assets.BundleKeys.USER_PHONE_KEY
import com.adv.ilook.model.util.extension.afterTextChanged
import com.adv.ilook.view.base.BaseFragment
import com.adv.ilook.view.base.BaseViewModel


import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

private const val TAG = "==>>OtpFragment"

@AndroidEntryPoint
class OtpFragment() : BaseFragment<FragmentOtpBinding>() {
    companion object {
        fun newInstance() = OtpFragment()
    }


    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentOtpBinding
        get() = FragmentOtpBinding::inflate
    private var _viewBinding: FragmentOtpBinding? = null
    private val viewModel: OtpViewModel by viewModels()
    val shareViewModel by viewModels<BaseViewModel>()
    override var nextScreenId_1 by Delegates.notNull<Int>()
    override var nextScreenId_2 by Delegates.notNull<Int>()
    override var previousScreenId by Delegates.notNull<Int>()
    var isSimValidationEnabled by Delegates.notNull<Boolean>()
    var isOtpEnabled by Delegates.notNull<Boolean>()
    var isAuthenticated by Delegates.notNull<Boolean>()
    private val onBackPress = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            isEnabled = true
            findNavControl()?.run {
                when (currentDestination?.id) {
                    R.id.otpFragment -> {
                        Toast.makeText(requireActivity(), "otp fragment", Toast.LENGTH_SHORT)
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
    var userName: String? = null
    var userPhone: String? = null
    var loginOtp: String? = null
    override fun setup(savedInstanceState: Bundle?) {
        Log.d(TAG, "setup:")
        arguments?.apply {
            userName = getString(USER_NAME_KEY)
            userPhone = getString(USER_PHONE_KEY)
            loginOtp = getString(LOGIN_OTP_KEY)
            Log.d(TAG, "setup: ${userName},${userPhone},${loginOtp}")
        }
        _viewBinding = binding
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.init { }
            uiReactiveAction()
        }
        viewLifecycleOwnerLiveData.observe(this) { lifecycleOwner ->
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                liveDataObserver(lifecycleOwner)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPress)
    }

    private fun liveDataObserver(lifecycleOwner: LifecycleOwner) {
        binding.apply {
            viewModel.tv_otp_header.observe(lifecycleOwner) {
                headerText.text = it
            }
            viewModel.tv_otp_helper_text.observe(lifecycleOwner) {
                subContentText.text = it
            }
            viewModel.et_otp_number_text.observe(lifecycleOwner) {
                otpInputTIEditText.hint = it
            }
            viewModel.bt_login_enable.observe(lifecycleOwner) {
                isOtpEnabled = it
                loginButton.isEnabled = it
            }

            viewModel.bt_login_text.observe(lifecycleOwner) {
                loginButton.text = it
            }
            viewModel.bt_sim_validation_enable.observe(lifecycleOwner) {
                isSimValidationEnabled = it
            }
            viewModel.nextScreenLiveData.observe(lifecycleOwner) {
                nextScreenId_1 = it
            }
            viewModel.prevScreenLiveData.observe(this@OtpFragment) {

                if (!(it == -1 or 0)) {
                    previousScreenId = it
                } else {
                    loginButton.showSnackbar(this.loginButton,
                        msg = "Can't found any screen",
                        actionMessage = "Ok", length = 2, action = { v1 ->

                        }, action2 = { v2 ->

                        }
                    )
                }

            }

            viewModel.signInResult.observe(lifecycleOwner) {
                isAuthenticated = it
                Log.d(TAG, "liveDataObserver: isAuthenticated -> ${isAuthenticated}")
                if (isAuthenticated) {
                  CoroutineScope(Dispatchers.Main).launch{
                      viewModel.saveUserData(userName!!, userPhone!!)
                  }
                    nav(nextScreenId_1)
                }else{
                    nav(previousScreenId)
                }

            }
        }
    }

    private fun uiReactiveAction() {
        binding.apply {
            if (loginOtp != null) {
                otpInputTIEditText.setText(loginOtp.toString())
            } else loginOtp = ""
            otpInputTIEditText.apply {
                doOnTextChanged { text, start, before, count ->
                    Log.d(
                       TAG,
                        "uiReactiveAction() called with: usernameText => text = $text, start = $start, before = $before, count = $count"
                    )

                }
                afterTextChanged { str ->
                    loginOtp=str.toString()
                }
            }
            loginButton.setOnClickListener {
                Toast.makeText(requireActivity(), "OTP sent successfully", Toast.LENGTH_SHORT).show()
                loginOtp=otpInputTIEditText.text.toString().trim()
                if (!(loginOtp.isNullOrEmpty()))
                    viewModel.verifyCode(loginOtp!!)
            }
        }
    }


}