package com.adv.ilook.view.ui.otpscreen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.adv.ilook.model.data.workflow.OtpScreen
import com.adv.ilook.model.db.remote.repository.apprepo.CommonRepository
import com.adv.ilook.model.util.network.NetworkHelper
import com.adv.ilook.model.util.responsehelper.Resource
import com.adv.ilook.model.util.responsehelper.UiStatusLogin
import com.adv.ilook.model.util.responsehelper.UiStatusOtp
import com.adv.ilook.view.base.BaseViewModel
import com.adv.ilook.view.base.BasicFunction
import com.adv.ilook.view.ui.splash.TypeOfData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.properties.Delegates

private const val TAG = "==>>OtpViewModel"
@HiltViewModel
class OtpViewModel  @Inject constructor(
    private val loginRepository: CommonRepository,
    private val networkHelper: NetworkHelper
) : BaseViewModel(networkHelper) {
    private var otpScreen by Delegates.notNull<OtpScreen>()
    private val _nextScreenLiveData = MutableLiveData<Int>()
    var nextScreenLiveData: LiveData<Int> = _nextScreenLiveData
    private val _prevScreenLiveData = MutableLiveData<Int>()
    var prevScreenLiveData: LiveData<Int> = _prevScreenLiveData
    override suspend fun init(function: (TypeOfData) -> Unit) {
        withContext(Dispatchers.IO) {
            getWorkflowFromJson {
                otpScreen = it.screens?.otpScreen!!
                Log.d(TAG, "init: prev ->${otpScreen.previousScreen.toString()}")
                Log.d(TAG, "init: next ->${otpScreen.nextScreen.toString()}")
                launch(Dispatchers.Main){
                    _nextScreenLiveData.postValue(BasicFunction.getScreens()[otpScreen.nextScreen.toString()] as Int)
                    _prevScreenLiveData.postValue( BasicFunction.getScreens()[otpScreen.previousScreen.toString()] as Int)
                }

                function(TypeOfData.INT)
            }

            launch (Dispatchers.Main){
                _tv_otp_header.postValue(otpScreen.views?.textView?.header?.text!!)
                _tv_otp_helper_text.postValue(otpScreen.views?.textView?.header?.helperText!!)
                _et_otp_number_text.postValue(otpScreen.views?.textView?.otpCode?.text!!)
                _bt_otp_enable.postValue(otpScreen.views?.textView?.otpCode?.enable!!)
                _bt_login_text.postValue(otpScreen.views?.buttonView?.verifyOtp?.text!!)
                _bt_login_enable.postValue(otpScreen.views?.buttonView?.verifyOtp?.enable!!)
                _toast_load_message.postValue(otpScreen.views?.toastView?.loading?.text!!)
                _toast_success_message.postValue(otpScreen.views?.toastView?.loginSuccess?.text!!)
                _toast_failure_message.postValue(otpScreen.views?.toastView?.loginFailure?.text!!)
            }
        }
    }


    private val _otpForm = MutableLiveData<UiStatusOtp>()
    val otpForm: LiveData<UiStatusOtp> = _otpForm

    private val _otpResult = MutableLiveData<Resource<Any>>()
    val otpResult: LiveData<Resource<Any>> = _otpResult

    private val _tv_otp_header = MutableLiveData<String>()
    val tv_otp_header: LiveData<String> = _tv_otp_header

    private val _tv_otp_helper_text = MutableLiveData<String>()
    val tv_otp_helper_text: LiveData<String> = _tv_otp_helper_text

    private val _et_otp_number_text = MutableLiveData<String>()
    val et_otp_number_text: LiveData<String> = _et_otp_number_text

    private val _bt_login_text = MutableLiveData<String>()
    val bt_login_text: LiveData<String> = _bt_login_text

    private val _bt_otp_enable = MutableLiveData<Boolean>()
    val bt_otp_enable: LiveData<Boolean> = _bt_otp_enable
    private val _bt_sim_validation_enable = MutableLiveData<Boolean>()
    val bt_sim_validation_enable: LiveData<Boolean> = _bt_sim_validation_enable

    private val _bt_login_enable = MutableLiveData<Boolean>()
    val bt_login_enable: LiveData<Boolean> = _bt_login_enable

    private val _toast_success_message = MutableLiveData<String>()
    var toast_success_message: LiveData<String> = _toast_success_message
    private val _toast_failure_message = MutableLiveData<String>()
    var toast_failure_message: LiveData<String> = _toast_failure_message
    private val _toast_load_message = MutableLiveData<String>()
    var toast_load_message: LiveData<String> = _toast_load_message
}