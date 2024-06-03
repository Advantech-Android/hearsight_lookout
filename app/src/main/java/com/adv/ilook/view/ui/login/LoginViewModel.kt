package com.adv.ilook.view.ui.login


import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.util.Patterns
import com.adv.ilook.R
import com.adv.ilook.model.data.workflow.LoginScreen
import com.adv.ilook.model.db.remote.firebase.realtimedatabase.FirebaseClient
import com.adv.ilook.model.db.remote.repository.CommonRepository
import com.adv.ilook.model.util.network.NetworkHelper
import com.adv.ilook.model.util.responsehelper.Resource
import com.adv.ilook.model.util.responsehelper.UiStatus

import com.adv.ilook.view.base.BaseViewModel
import com.adv.ilook.view.base.BasicFunction

import com.adv.ilook.view.ui.splash.TypeOfData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.properties.Delegates

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: CommonRepository,
    private val networkHelper: NetworkHelper
) : BaseViewModel(networkHelper) {
    override suspend fun init(function: (TypeOfData) -> Unit) {
        withContext(Dispatchers.IO) {
            getWorkflowFromJson {
                loginScreen = it.screens?.loginScreen!!
                nextScreenId = BasicFunction.getScreens()[loginScreen.nextScreen] as Int
                previousScreenId = BasicFunction.getScreens()[loginScreen.previousScreen] as Int
                when (nextScreenId) {
                    is Int -> {
                        _nextScreenLiveData.postValue(nextScreenId as Int)
                        function(TypeOfData.INT)
                    }
                    else -> {
                        function(TypeOfData.ANY)
                    }
                }
            }
            launch {
                _tv_login_header.postValue(loginScreen.views?.textView?.header?.text!!)
                _tv_username.postValue(loginScreen.views?.textView?.userName?.text!!)
                _tv_phone_number.postValue(loginScreen.views?.textView?.mobileNumber?.text!!)
                _bt_login_text.postValue(loginScreen.views?.buttonView?.login?.text!!)
            }
        }
    }

   suspend fun login(username: String, phone: String) {
        _loginResult.postValue(Resource.loading(true))
       withContext(Dispatchers.IO) {
           if (networkHelper.isNetworkConnected()) {
               if (isUserNameValid(username) && isPhoneNumberValid(phone)) {
                   loginRepository.login(username, phone) {
                       _loginResult.postValue(Resource.success("Login Success"))
                   }
               } else {
                   _loginResult.postValue(Resource.loading(false))
                   _loginResult.postValue(Resource.error(msg = R.string.login_failed, null))
               }


           } else {
               _loginResult.postValue(Resource.error(msg = R.string.network_error, null))
           }
       }


    }

    fun loginDataChange(username: String, phone: String) {
        if (!isUserNameValid(username)) {
            _loginForm.postValue(UiStatus.LoginFormState(usernameError = R.string.invalid_username))
        } else if (!isPhoneNumberValid(phone)) {
            _loginForm.postValue(UiStatus.LoginFormState(phoneError = R.string.invalid_phonenumber))
        } else {
            _loginForm.postValue(UiStatus.LoginFormState(message = R.string.valid_sucess))
            _loginForm.postValue(UiStatus.LoginFormState(isDataValid = true))
        }
    }

    private fun isPhoneNumberValid(phone: String): Boolean {
        if (phone.length != 10) {
            return false
        } else {
            var phoneTemp = "+91$phone"
            return Patterns.PHONE.matcher(phoneTemp).matches()
        }
    }

    private fun isUserNameValid(username: String): Boolean {
        return username.trim().length > 3
    }

    override fun callOtherActivity(activity: Activity, msg: String) {

    }


    private val _loginForm = MutableLiveData<UiStatus>()
    val loginFormState: LiveData<UiStatus> = _loginForm

    private val _loginResult = MutableLiveData<Resource<Any>>()
    val loginResult: LiveData<Resource<Any>> = _loginResult

    private var nextScreenId by Delegates.notNull<Int>()
    private var previousScreenId by Delegates.notNull<Int>()
    private lateinit var loginScreen: LoginScreen
    val username = MutableLiveData<String>()
    private val _nextScreenLiveData = MutableLiveData<Int>()
    var nextScreenLiveData: LiveData<Int> = _nextScreenLiveData

    private val _tv_login_header = MutableLiveData<String>()
    var tv_login_header: LiveData<String> = _tv_login_header
    private val _tv_username = MutableLiveData<String>()
    var tv_username: LiveData<String> = _tv_username
    private val _tv_phone_number = MutableLiveData<String>()
    var tv_phone_number: LiveData<String> = _tv_phone_number

    private val _bt_login_text = MutableLiveData<String>()
    var bt_login_text: LiveData<String> = _bt_login_text

}