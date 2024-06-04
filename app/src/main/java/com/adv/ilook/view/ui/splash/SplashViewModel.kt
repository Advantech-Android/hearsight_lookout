package com.adv.ilook.view.ui.splash

import android.app.Activity
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adv.ilook.model.util.network.NetworkHelper
import com.adv.ilook.view.base.BaseViewModel
import com.adv.ilook.view.base.BasicFunction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


enum class TypeOfData {
    INT, STRING, ANY, ELSE
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val networkHelper: NetworkHelper) : BaseViewModel(networkHelper) {
    private val _nextScreenLiveData = MutableLiveData<Int>()
    var nextScreenLiveData: LiveData<Int> = _nextScreenLiveData
    override fun callOtherActivity(activity: Activity, msg: String) {

    }

    override suspend fun init(function: (TypeOfData) -> Unit) {
        withContext(Dispatchers.IO) {
            getWorkflowFromJson {
                val nextPage=BasicFunction.getScreens()[it.screens?.splashScreen?.nextScreen]
                when (nextPage) {
                    is Int -> {
                        _nextScreenLiveData.postValue(nextPage as Int)
                        function(TypeOfData.INT)
                    }
                    else -> {
                        function(TypeOfData.ANY)
                    }
                }
            }
        }

    }

}