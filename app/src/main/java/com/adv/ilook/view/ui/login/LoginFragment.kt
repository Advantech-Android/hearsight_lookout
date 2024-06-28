package com.adv.ilook.view.ui.login

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.adv.ilook.R
import com.adv.ilook.databinding.FragmentLoginBinding
import com.adv.ilook.model.util.assets.BundleKeys.LOGIN_OTP_KEY
import com.adv.ilook.model.util.assets.BundleKeys.USER_NAME_KEY
import com.adv.ilook.model.util.assets.BundleKeys.USER_PHONE_KEY
import com.adv.ilook.model.util.extension.afterTextChanged
import com.adv.ilook.model.util.responsehelper.Status
import com.adv.ilook.model.util.responsehelper.UiStatusLogin
import com.adv.ilook.view.base.BaseFragment
import com.adv.ilook.view.base.BaseViewModel
import com.bumptech.glide.Glide
import com.google.firebase.auth.PhoneAuthCredential
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


    var codeSend by Delegates.notNull<Boolean>()
    var verificationCompleted by Delegates.notNull<PhoneAuthCredential>()
    var verificationFailed by Delegates.notNull<String>()
    var isAuthenticated by Delegates.notNull<Boolean>()


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
        // Check for SMS read permissions

    }

    private fun liveDataObserver(lifecycleOwner: LifecycleOwner) {
        binding.apply {

            viewModel.loginFormState.observe(lifecycleOwner,
                Observer { loginFormState ->
                    val loginState: UiStatusLogin = loginFormState ?: return@Observer
                    when (loginState) {
                        is UiStatusLogin.LoginFormState -> {
                            // disable login button unless both username / password is valid
                            generateOtpButton.isEnabled = loginState.isDataValid
                            if (loginState.usernameError != null) {
                                usernameText.error = loginState.usernameError.toString()
                            }
                            if (loginState.phoneError != null) {
                                phoneText.error = loginState.phoneError.toString()
                            }
                        }
                    }
                })

            viewModel.loginResult.observe(lifecycleOwner) { it ->
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

                        Log.d(TAG, "liveDataObserver: Success")
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

                if (!(it == -1 or 0)) {
                    previousScreenId = it
                } else {
                    generateOtpButton.showSnackbar(this.generateOtpButton,
                        msg = "Can't found any screen",
                        actionMessage = "Ok", length = 2, action = { v1 ->

                        }, action2 = { v2 ->

                        }
                    )
                }
            }

            viewModel.nextScreenLiveData.observe(lifecycleOwner) {
                nextScreenId_1 = it
            }

            viewModel.codeSent.observe(lifecycleOwner) {
                codeSend = it
                Log.d(TAG, "liveDataObserver: ${it}")
                nav(nextScreenId_1, Bundle().apply {
                    val userName = usernameText.text.toString().trim()
                    val userPhone = phoneText.text.toString().trim()
                    putString(USER_NAME_KEY, userName)
                    putString(USER_PHONE_KEY, userPhone)
                    putString(LOGIN_OTP_KEY, null)
                })
                Toast.makeText(requireActivity(), "OTP sent successfully", Toast.LENGTH_SHORT)
                    .show()
            }

            viewModel.verificationCompleted.observe(lifecycleOwner) {
                verificationCompleted = it
                val code = verificationCompleted.smsCode
                Log.d(TAG, "liveDataObserver:Otp verification completed -> ${code}")
//                phoneText.setText(code.toString())

                if (code != null) {
                    nav(nextScreenId_1, Bundle().apply {
                        val userName = usernameText.text.toString().trim()
                        val userPhone = phoneText.text.toString().trim()
                        putString(USER_NAME_KEY, userName)
                        putString(USER_PHONE_KEY, userPhone)
                        putString(LOGIN_OTP_KEY, code)
                    })
                }
            }

            viewModel.verificationFailed.observe(lifecycleOwner) {
                verificationFailed = it
                Log.d(TAG, "liveDataObserver: ${verificationFailed}")
                updateUiWithUser(it)
            }

            viewModel.signInResult.observe(lifecycleOwner) {
                isAuthenticated = it
                Log.d(TAG, "liveDataObserver: isAuthenticated -> ${isAuthenticated}")

            }
        }
        requestSMSPermissions()

    }

    private fun requestSMSPermissions() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.RECEIVE_SMS
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.READ_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS),
                1
            )
        }
    }

    private fun uiReactiveAction() {
        binding.apply {

            usernameText.apply {
                doOnTextChanged { text, start, before, count ->
                    Log.d(
                        TAG,
                        "uiReactiveAction() called with: usernameText => text = $text, start = $start, before = $before, count = $count"
                    )
                    viewModel.loginDataChange(
                        usernameText.text.toString(),
                        phoneText.text.toString()
                    )
                }
                afterTextChanged { str ->
                    Log.d(TAG, "uiReactiveAction: $str")

                    viewModel.loginDataChange(
                        usernameText.text.toString(),
                        phoneText.text.toString()
                    )
                }
            }
            phoneText.apply {
                doOnTextChanged { text, start, before, count ->
                    Log.d(
                        TAG,
                        "uiReactiveAction() called with: phoneText => text = $text, start = $start, before = $before, count = $count"
                    )
                    viewModel.loginDataChange(
                        usernameText.text.toString(),
                        phoneText.text.toString()
                    )
                }
                afterTextChanged {
                    viewModel.loginDataChange(
                        usernameText.text.toString(),
                        phoneText.text.toString()
                    )
                }
                setOnEditorActionListener { _, actionId, _ ->
                    when (actionId) {
                        EditorInfo.IME_ACTION_DONE ->
                            activityListener.onRequestPermissionListener(
                                binding,
                                arrayListOf(
                                    Manifest.permission.RECORD_AUDIO,
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.READ_SMS,
                                    Manifest.permission.RECEIVE_SMS
                                )
                            ) {
                                lifecycleScope.launch(Dispatchers.IO) {

                                    viewModel.loginDataChange(
                                        usernameText.text.toString(),
                                        phoneText.text.toString()
                                    )
                                    viewModel.login(
                                        requireActivity(),
                                        usernameText.text.toString(),
                                        phoneText.text.toString()
                                    )
                                }
                            }
                    }
                    false
                }
            }

            generateOtpButton.setOnClickListener {
                activityListener.onRequestPermissionListener(
                    binding,
                    arrayListOf(
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_SMS,
                        Manifest.permission.RECEIVE_SMS
                    )
                ) {
                    val userName = usernameText.text.toString().trim()
                    val phone = phoneText.text.toString().trim()
                    viewModel.loginDataChange(
                        userName,
                        phone
                    )
                    loadingImage.visibility = View.VISIBLE
                    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                        viewModel.login(requireActivity(), userName, phone)
                    }

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