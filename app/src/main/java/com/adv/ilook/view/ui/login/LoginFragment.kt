package com.adv.ilook.view.ui.login

import android.Manifest
import android.app.Activity
import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.adv.ilook.R
import com.adv.ilook.databinding.FragmentLoginBinding
import com.adv.ilook.databinding.FragmentSplashBinding
import com.adv.ilook.model.util.extension.afterTextChanged
import com.adv.ilook.model.util.responsehelper.Resource
import com.adv.ilook.model.util.responsehelper.Status
import com.adv.ilook.model.util.responsehelper.UiStatus
import com.adv.ilook.view.base.BaseFragment
import com.adv.ilook.view.base.BaseViewModel
import com.adv.ilook.view.ui.MainActivity2

import com.adv.ilook.view.ui.splash.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

private const val TAG = "==>>LoginFragment"

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>() {


    private var _viewBinding: FragmentLoginBinding? = null
    val viewModel by viewModels<LoginViewModel>()
    val shareViewModel by viewModels<BaseViewModel>()
    override var nextScreenId_1 by Delegates.notNull<Int>()
    override var nextScreenId_2 by Delegates.notNull<Int>()
    override var previousScreenId by Delegates.notNull<Int>()
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentLoginBinding
        get() = FragmentLoginBinding::inflate

    override fun setup(savedInstanceState: Bundle?) {
        Log.d(TAG, "setup: ")
        _viewBinding = binding
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPress)
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.init { }
        }
        uiReactiveAction()
        liveDataObserver()

    }
    // Create an OnBackPressedCallback to handle the back button event
    private val   onBackPress = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            isEnabled = true
            findNavControl()?.run {
                when (currentDestination?.id) {
                    R.id.loginFragment -> {
                        Toast.makeText(requireActivity(), "loginFragment", Toast.LENGTH_SHORT)
                            .show()
                        nav(previousScreenId)
                    }else -> {
                       requireActivity().finish()
                    }

                }
            }
        }
    }

    private fun liveDataObserver() {
        binding.apply {
            viewModel.loginFormState.observe(viewLifecycleOwner,
                Observer { loginFormState ->
                    val loginState: UiStatus = loginFormState ?: return@Observer
                    when (loginState) {
                        is UiStatus.Idle -> {}
                        is UiStatus.Loading -> {}
                        is UiStatus.Success -> {}
                        is UiStatus.Error -> {}
                        is UiStatus.OtpFormState -> {}
                        is UiStatus.LoginFormState -> {
                            // disable login button unless both username / password is valid
                            generateOtpButton.isEnabled = loginState.isDataValid
                            if (loginState.usernameError != null) {
                                usernameText.error = getString(loginState.usernameError)
                            }
                            if (loginState.phoneError != null) {
                                phoneText.error = getString(loginState.phoneError)
                            }
                        }


                    }
                })

            viewModel.loginResult.observe(viewLifecycleOwner,
                Observer { it ->
                    val loginResult = it ?: return@Observer
                    when (it.status) {
                        Status.LOADING -> {
                            if (it.data == false) loadingGif.visibility =
                                View.GONE else loadingGif.visibility = View.VISIBLE
                        }

                        Status.ERROR -> {
                            showLoginFailed(loginResult.message as Int)
                        }

                        Status.SUCCESS -> {
                            updateUiWithUser(loginResult.data as String)
                        }
                    }

                })
            viewModel.tv_login_header.observe(viewLifecycleOwner) {
                binding.loginText.text = it
            }
            viewModel.tv_username.observe(viewLifecycleOwner) {
                binding.usernameTILayout.hint = it
            }
            viewModel.tv_phone_number.observe(viewLifecycleOwner) {
                binding.phoneTILayout.hint = it
            }
            viewModel.bt_login_text.observe(viewLifecycleOwner) {
                binding.generateOtpButton.text = it
            }
            viewModel.prevScreenLiveData.observe(viewLifecycleOwner) {
                previousScreenId = it
            }
        }

    }

    private fun uiReactiveAction() {
        binding.apply {
            usernameText.afterTextChanged {
                viewModel.loginDataChange(
                    usernameText.text.toString(),
                    phoneText.text.toString()
                )
            }
            phoneText.apply {
                afterTextChanged {
                    viewModel.loginDataChange(
                        usernameText.text.toString(),
                        phoneText.text.toString()
                    )
                }
                setOnEditorActionListener { _, actionId, _ ->
                    when (actionId) {
                        EditorInfo.IME_ACTION_DONE ->
                            viewLifecycleOwner
                                .lifecycleScope.launch(Dispatchers.IO) {
                                    viewModel.login(
                                        usernameText.text.toString(),
                                        phoneText.text.toString()
                                    )
                                }
                    }
                    false
                }
            }

            generateOtpButton.setOnClickListener {
                activityListener.onRequestPermissionListener(
                    binding,
                    arrayListOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA)
                ) {
                    loading.visibility = View.VISIBLE
                    viewLifecycleOwner
                        .lifecycleScope.launch(Dispatchers.IO) {
                            viewModel.login(usernameText.text.toString(), phoneText.text.toString())
                        }
                    //  shareViewModel.actionLiveData.postValue("compose")
                }

            }
        }

    }


    private fun updateUiWithUser(model: String) {
        val welcome = getString(R.string.welcome) + model
        // TODO : initiate successful logged in experience
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, welcome, Toast.LENGTH_LONG).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }
}