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
import androidx.lifecycle.LifecycleOwner
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
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) { uiReactiveAction() }
        // viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) { liveDataObserver() }
        viewLifecycleOwnerLiveData.observe(this) { lifecycleOwner ->
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                liveDataObserver(lifecycleOwner)
            }
        }

    }

    // Create an OnBackPressedCallback to handle the back button event
    private val onBackPress = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            isEnabled = true
            findNavControl()?.run {
                when (currentDestination?.id) {
                    R.id.loginFragment -> {
                        Toast.makeText(requireActivity(), "loginFragment", Toast.LENGTH_SHORT)
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

    private fun liveDataObserver(lifecycleOwner: LifecycleOwner) {
        binding.apply {

            viewModel.loginFormState.observe(lifecycleOwner,
                Observer { loginFormState ->
                    val loginState: UiStatus = loginFormState ?: return@Observer
                    when (loginState) {
                        is UiStatus.Idle -> {}
                        is UiStatus.Loading -> {}
                        is UiStatus.Success -> {
                            Log.d(TAG, "liveDataObserver: ------")
                        }

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

            viewModel.loginResult.observe(
                lifecycleOwner
            ) { it ->

                val loginResult = it ?: return@observe
                Log.d(TAG, "liveDataObserver: $loginResult")
                Log.d(TAG, "liveDataObserver: $it")
                when (loginResult.status) {
                    Status.LOADING -> {

                        if (loginResult.data == false) {
                            loadingImage.visibility = View.GONE
                            innerContainer.alpha = 1f
                            innerContainer.isEnabled = true

                        } else {
                            innerContainer.alpha = 0.5f
                            innerContainer.isEnabled = false
                            loadingImage.visibility = View.VISIBLE
                            Glide.with(requireActivity()).load(R.drawable.loading)
                                .into(loadingImage)
                        }
                    }

                    Status.ERROR -> {
                        Log.d(TAG, "ERROR----")
                        showLoginFailed(loginResult.message as Int)

                    }

                    Status.SUCCESS -> {
                        nav(nextScreenId_1)
                        updateUiWithUser(loginResult.data as String)
                    }
                }


            }
            viewModel.tv_login_header.observe(lifecycleOwner) {
                binding.loginText.text = it
            }
            viewModel.tv_username.observe(lifecycleOwner) {
                binding.usernameTILayout.hint = it
            }
            viewModel.tv_phone_number.observe(lifecycleOwner) {
                binding.phoneTILayout.hint = it
            }
            viewModel.bt_login_text.observe(lifecycleOwner) {
                binding.generateOtpButton.text = it
            }
            viewModel.prevScreenLiveData.observe(lifecycleOwner) {
                previousScreenId = it
            }

            viewModel.nextScreenLiveData.observe(lifecycleOwner) {
                nextScreenId_1 = it
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
                        lifecycleScope.launch(Dispatchers.IO) {
                            viewModel.loginDataChange(
                                usernameText.text.toString(),
                                phoneText.text.toString()
                            )
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
                    viewModel.loginDataChange(
                        usernameText.text.toString(),
                        phoneText.text.toString()
                    )
                    loadingImage.visibility = View.VISIBLE

                    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
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