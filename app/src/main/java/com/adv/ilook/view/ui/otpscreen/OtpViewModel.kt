package com.adv.ilook.view.ui.otpscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.adv.ilook.model.data.workflow.OtpScreen
import com.adv.ilook.model.db.remote.repository.apprepo.CommonRepository
import com.adv.ilook.model.util.network.NetworkHelper
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
                _nextScreenLiveData.postValue(BasicFunction.getScreens()[otpScreen.nextScreen] as Int)
                _prevScreenLiveData.postValue( BasicFunction.getScreens()[otpScreen.previousScreen] as Int)
                function(TypeOfData.INT)
            }
            launch {
               /* _tv_login_header.postValue(otpScreen.views?.textView?.header?.text!!)
                _tv_username.postValue(otpScreen.views?.textView?.userName?.text!!)
                _tv_phone_number.postValue(otpScreen.views?.textView?.mobileNumber?.text!!)
                _bt_login_text.postValue(otpScreen.views?.buttonView?.login?.text!!)*/
            }
        }
    }

}