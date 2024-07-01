package com.adv.ilook.view.ui.login


import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.util.Patterns
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import com.adv.ilook.R
import com.adv.ilook.model.data.workflow.LoginScreen
import com.adv.ilook.model.db.remote.repository.apprepo.CommonRepository
import com.adv.ilook.model.db.remote.repository.apprepo.SeeForMeRepo
import com.adv.ilook.model.util.assets.FirebaseKeys
import com.adv.ilook.model.util.assets.UserStatus
import com.adv.ilook.model.util.network.NetworkHelper
import com.adv.ilook.model.util.responsehelper.Resource

import com.adv.ilook.model.util.responsehelper.UiStatusLogin

import com.adv.ilook.view.base.BaseViewModel
import com.adv.ilook.view.base.BasicFunction

import com.adv.ilook.view.ui.splash.TypeOfData
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.properties.Delegates

private const val TAG = "==>>LoginViewModel"

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: SeeForMeRepo,
    private val networkHelper: NetworkHelper
) : BaseViewModel(networkHelper) {
    private lateinit var addPhoneCountry: String

    // private lateinit var loginScreen: LoginScreen
    private var loginScreen by Delegates.notNull<LoginScreen>()
    private val _nextScreenLiveData = MutableLiveData<Int>()
    var nextScreenLiveData: LiveData<Int> = _nextScreenLiveData
    private val _prevScreenLiveData = MutableLiveData<Int>()
    var prevScreenLiveData: LiveData<Int> = _prevScreenLiveData

    override suspend fun init(function: (TypeOfData) -> Unit) {
        withContext(Dispatchers.IO) {
            getWorkflowFromJson {
                loginScreen = it.screens?.loginScreen!!
                Log.d(TAG, "init: prev ->${loginScreen.previousScreen.toString()}")
                Log.d(TAG, "init: next ->${loginScreen.nextScreen.toString()}")
                _nextScreenLiveData.postValue(BasicFunction.getScreens()[loginScreen.nextScreen] as Int)
                _prevScreenLiveData.postValue(BasicFunction.getScreens()[loginScreen.previousScreen] as Int)
                function(TypeOfData.INT)
            }
            launch {
                _tv_login_header.postValue(loginScreen.views?.textView?.header?.text!!)
                _tv_username.postValue(loginScreen.views?.textView?.userName?.text!!)
                _tv_phone_number.postValue(loginScreen.views?.textView?.mobileNumber?.text!!)
                _validation_message.postValue(loginScreen.views?.toastView?.loginSuccess?.text!!)
                _validation_name_error.postValue(loginScreen.views?.textView?.userName?.helperText!!)
                _validation_phone_error.postValue(loginScreen.views?.textView?.mobileNumber?.helperText!!)
                _bt_login_text.postValue(loginScreen.views?.buttonView?.login?.text!!)

            }
        }
    }

    fun loginEmit(username: String, phone: String): Flow<Resource<Any>> {
        return flow {

            //    emit(Resource.loading(true))
            if (networkHelper.isNetworkConnected()) {
                if (isUserNameValid(username) && isPhoneNumberValid(phone)) {
                    try {
                        //  withContext(Dispatchers.IO) {
                        val result = loginRepository.login(
                            username,
                            phone,
                            UserStatus.ONLINE.name,
                            isLogged = true
                        ) { isComplete, message ->
                            //   emit(Resource.success(it))
                            //  emit(Resource.success("login success"))
                        }
                        // emit(Resource.success(result))
                    } catch (e: Exception) {
                        emit(Resource.error(custom_message = R.string.login_failed, e.message))
                    }
                } else {
                    emit(
                        Resource.error(
                            custom_message = R.string.login_failed,
                            "name and phone is invalid"
                        )
                    )
                }
            } else {
                emit(Resource.error(custom_message = R.string.network_error, "network is needed"))
            }
//            emit(Resource.loading(false))
        }.flowOn(Dispatchers.IO)
    }

    fun loginFlow(username: String, phone: String) {

        viewModelScope.launch {
            loginEmit(username, phone).onStart {
                Log.d(TAG, "onStart")
                _loginResult.postValue(Resource.loading(isLoading = true, data = "Please wait..."))
            }
                .onCompletion {
                    Log.d(TAG, "onCompletion")
                    _loginResult.postValue(
                        Resource.loading(
                            isLoading = false,
                            data = "Completed..."
                        )
                    )
                }
                .catch { e ->
                    Log.d(TAG, "catch ${e.message}")
                    _loginResult.postValue(
                        Resource.error(
                            custom_message = R.string.login_failed,
                            null
                        )
                    )

                }
                .collect { resource ->
                    Log.d(TAG, "collect $resource")
                    runBlocking { _loginResult.postValue(resource) }

                }
        }
    }

    suspend fun login(activity: Activity, username: String, phone: String) {
        //  loginFlow(username, phone)

        runBlocking {
            withContext(Dispatchers.Main) {

                _loginResult.postValue(Resource.loading(isLoading = true, "Please wait..."))
            }
            //  delay(1000)
            if (networkHelper.isNetworkConnected()) {
                if (isUserNameValid(username) && isPhoneNumberValid(phone)) {

                    try {
                        withContext(Dispatchers.IO) {
                            /*     val result = loginRepository.login(username, addPhoneCountry) {
                                     _loginResult.postValue(Resource.success(it))
                                 }*/
                            sendVerificationCode(activity, addPhoneCountry)
                        }

                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            _loginResult.postValue(
                                Resource.loading(
                                    isLoading = false,
                                    "${e.message}..."
                                )
                            )
                            _loginResult.postValue(
                                Resource.error(
                                    custom_message = R.string.login_failed,
                                    null
                                )
                            )
                        }
                    } finally {
                        // launch {  _loginResult.postValue(Resource.loading(isLoading = false,"Completed...")) }
                    }

                } else {
                    withContext(Dispatchers.Main) {
                        _loginResult.postValue(Resource.loading(isLoading = false, "Completed..."))
                    }
                    withContext(Dispatchers.Main) {
                        _loginResult.postValue(
                            Resource.error(
                                custom_message = R.string.login_failed,
                                null
                            )
                        )
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    _loginResult.postValue(Resource.loading(isLoading = false, "Completed..."))
                    _loginResult.postValue(
                        Resource.error(
                            custom_message = R.string.network_error,
                            null
                        )
                    )
                }
            }
        }

    }

    suspend fun sendVerificationCode(activity: Activity, phone: String) {
        withContext(Dispatchers.Main) {
            launch(Dispatchers.IO) {
                loginRepository.sendVerificationCode(activity, phone, object : PhoneAuthProvider
                .OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                        Log.d("__TAG", "sendVerificationCode: Complete function")
                        launch { _verificationCompleted.value = credential }
                    }


                    override fun onVerificationFailed(e: FirebaseException) {
                        launch { _verificationFailed.value = e.message }
                        Log.e(TAG, "onVerificationFailed: Message ->${e.message}")
                        //   _loginResult.postValue(Resource.loading(data=false))
                    }

                    override fun onCodeSent(
                        verificationId: String,
                        token: PhoneAuthProvider.ForceResendingToken
                    ) {
                        Log.d(
                            TAG,
                            "onCodeSent() called with: verificationId = $verificationId, token = $token"
                        )
                        launch {
                            _loginResult.postValue(Resource.loading(data = false))
                            loginRepository.setVerificationIdAndToken(verificationId, token)
                            _codeSent.value = true
                        }
                        //AD8T5Iv1Lo9Yo833gE2gWV20sSNEc-8RdphK_OU1IhkQr0hULuN752nHbSh7O9bW1XQAJ33w3nYBh6IDOvFdpTjtAsrC1tjjNar9QINwPZBhQSwlryq9Rc558hFBQD4LUOS8UkvyfkGVGtqzmzmDWCW7XRCp1eS8HA ==> verificationId
                        //com.google.firebase.auth.PhoneAuthProvider$ForceResendingToken@1363b7b  ==> token
                    }
                })
            }

        }
    }

    fun verifyCode(code: String) {
        try {
            val credential = loginRepository.verifyCode(code)
            signInWithCredential(credential)
        } catch (e: Exception) {
            _verificationFailed.value = e.message
        }
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        loginRepository.signInWithCredential(credential) { user, exception ->
            if (user != null) {
                _signInResult.value = true
            } else {
                _signInResult.value = false
                exception?.message?.let { _verificationFailed.value = it }
            }
        }
    }

    fun loginDataChange(username: String, phone: String) {
        if (!isUserNameValid(username)) {
            _loginForm.postValue(UiStatusLogin.LoginFormState(usernameError = validation_name_error.value))
        } else if (!isPhoneNumberValid(phone)) {
            _loginForm.postValue(UiStatusLogin.LoginFormState(phoneError = validation_phone_error.value))
        } else {
            _loginForm.postValue(UiStatusLogin.LoginFormState(message = validation_message.value))
            _loginForm.postValue(UiStatusLogin.LoginFormState(isDataValid = true))
        }
    }

    private fun isPhoneNumberValid(phone: String): Boolean {
        if (phone.length != 10) {
            return false
        } else {
            addPhoneCountry = "+91$phone"
            return Patterns.PHONE.matcher(addPhoneCountry).matches()
        }
    }

    private fun isUserNameValid(username: String): Boolean {
        return username.trim().length > 3
    }

    override fun callOtherActivity(activity: Activity, msg: String) {

    }


    private val _loginForm = MutableLiveData<UiStatusLogin>()
    val loginFormState: LiveData<UiStatusLogin> = _loginForm

    private val _loginResult = MutableLiveData<Resource<Any>>()
    val loginResult: LiveData<Resource<Any>> = _loginResult

    private val _codeSent = MutableLiveData<Boolean>()
    val codeSent: LiveData<Boolean> = _codeSent

    private val _verificationCompleted = MutableLiveData<PhoneAuthCredential>()
    val verificationCompleted: LiveData<PhoneAuthCredential> = _verificationCompleted

    private val _verificationFailed = MutableLiveData<String>()
    val verificationFailed: LiveData<String> = _verificationFailed

    private val _signInResult = MutableLiveData<Boolean>()
    val signInResult: LiveData<Boolean> = _signInResult

    val username = MutableLiveData<String>()


    private val _tv_login_header = MutableLiveData<String>()
    var tv_login_header: LiveData<String> = _tv_login_header
    private val _tv_username = MutableLiveData<String>()
    var tv_username: LiveData<String> = _tv_username
    private val _tv_phone_number = MutableLiveData<String>()
    var tv_phone_number: LiveData<String> = _tv_phone_number

    private val _validation_message = MutableLiveData<String>()
    var validation_message: LiveData<String> = _validation_message
    private val _validation_phone_error = MutableLiveData<String>()
    var validation_phone_error: LiveData<String> = _validation_phone_error
    private val _validation_name_error = MutableLiveData<String>()
    var validation_name_error: LiveData<String> = _validation_name_error


    private val _bt_login_text = MutableLiveData<String>()
    var bt_login_text: LiveData<String> = _bt_login_text

}