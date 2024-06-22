package com.adv.ilook.view.ui.login


import android.app.Activity
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.util.Patterns
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import kotlin.properties.Delegates
private const val  TAG="==>>LoginViewModel"
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: CommonRepository,
    private val networkHelper: NetworkHelper
) : BaseViewModel(networkHelper) {
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
                _nextScreenLiveData.postValue(BasicFunction.getScreens()[loginScreen.nextScreen] as Int)
                _prevScreenLiveData.postValue(BasicFunction.getScreens()[loginScreen.previousScreen] as Int)
                function(TypeOfData.INT)
            }
            launch {
                _tv_login_header.postValue(loginScreen.views?.textView?.header?.text!!)
                _tv_username.postValue(loginScreen.views?.textView?.userName?.text!!)
                _tv_phone_number.postValue(loginScreen.views?.textView?.mobileNumber?.text!!)
                _bt_login_text.postValue(loginScreen.views?.buttonView?.login?.text!!)
            }
        }
    }

    fun loginEmit(username: String, phone: String): Flow<Resource<Any>> {
        return flow {
           
            emit(Resource.loading(true))
            if (networkHelper.isNetworkConnected()) {
                if (isUserNameValid(username) && isPhoneNumberValid(phone)) {
                    try {
                       //  withContext(Dispatchers.IO) {
                             val result = loginRepository.login(username, phone){

                            }
                            emit(Resource.success(result))
                       // }
                       // emit(Resource.success(result))
                    } catch (e: Exception) {

                        emit(Resource.error(msg = R.string.login_failed, e.message))

                    }



                } else {
                    emit(Resource.error(msg = R.string.login_failed, "name and phone is invalid"))
                }
            } else {
                emit(Resource.error(msg = R.string.network_error, "network is needed"))
            }
            emit(Resource.loading(false))
        }.flowOn(Dispatchers.IO)
    }

    fun loginFlow(username: String, phone: String) {
        
        viewModelScope.launch {
            loginEmit(username, phone).
                    onStart {
                        Log.d(TAG,"onStart")
                        _loginResult.postValue(Resource.loading(true)) }
                .onCompletion {    Log.d(TAG,"onCompletion")
                    _loginResult.postValue(Resource.loading(false))}
                .catch { e ->
                    Log.d(TAG,"catch ${e.message}")
                    _loginResult.postValue(Resource.error(msg = R.string.login_failed, null))

                }
                .collect { resource ->
                    Log.d(TAG,"collect $resource")
                    _loginResult.postValue(resource)

                }
        }
    }

    suspend fun login(username: String, phone: String) {
        loginFlow(username, phone)
   /*     withContext(Dispatchers.Main) {

            _loginResult.postValue(Resource.loading(true))
        }
        //  delay(1000)
        if (networkHelper.isNetworkConnected()) {
            if (isUserNameValid(username) && isPhoneNumberValid(phone)) {

                try {
                    withContext(Dispatchers.IO) {
                        val result = loginRepository.login(username, phone) {
                            _loginResult.postValue(Resource.loading(false))
                        }
                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        _loginResult.postValue(Resource.loading(false))
                        _loginResult.postValue(Resource.error(msg = R.string.login_failed, null))
                    }
                }

            } else {
                withContext(Dispatchers.Main) {
                    _loginResult.postValue(Resource.loading(false))
                }
                withContext(Dispatchers.Main) {
                    _loginResult.postValue(Resource.error(msg = R.string.login_failed, null))


                    //
                }
            }
        } else {
            withContext(Dispatchers.Main) {
                _loginResult.postValue(Resource.loading(false))

                _loginResult.postValue(Resource.error(msg = R.string.network_error, null))
            }
        }*/

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

    val username = MutableLiveData<String>()


    private val _tv_login_header = MutableLiveData<String>()
    var tv_login_header: LiveData<String> = _tv_login_header
    private val _tv_username = MutableLiveData<String>()
    var tv_username: LiveData<String> = _tv_username
    private val _tv_phone_number = MutableLiveData<String>()
    var tv_phone_number: LiveData<String> = _tv_phone_number

    private val _bt_login_text = MutableLiveData<String>()
    var bt_login_text: LiveData<String> = _bt_login_text

}