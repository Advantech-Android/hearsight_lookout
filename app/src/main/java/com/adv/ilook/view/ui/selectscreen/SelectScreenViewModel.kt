package com.adv.ilook.view.ui.selectscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.adv.ilook.model.data.workflow.SelectScreenType
import com.adv.ilook.model.db.remote.repository.CommonRepository
import com.adv.ilook.model.util.network.NetworkHelper
import com.adv.ilook.view.base.BaseViewModel
import com.adv.ilook.view.base.BasicFunction
import com.adv.ilook.view.ui.splash.TypeOfData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.properties.Delegates

@HiltViewModel
class SelectScreenViewModel(private val loginRepository: CommonRepository,
                            private val networkHelper: NetworkHelper
): BaseViewModel(networkHelper) {
    private val _nextScreenLiveData = MutableLiveData<Int>()
    var nextScreenLiveData: LiveData<Int> = _nextScreenLiveData
    private val _selectScreenType = MutableLiveData<SelectScreenType>()
    var selectScreenType:LiveData<SelectScreenType> = _selectScreenType
    private var nextScreenId by Delegates.notNull<Int>()
    private var previousScreenId by Delegates.notNull<Int>()
    private val _tv_select_screen_header=MutableLiveData<String>()
    var tv_select_screen_header:LiveData<String> = _tv_select_screen_header
    override suspend fun init(function: (TypeOfData) -> Unit) {
        withContext(Dispatchers.IO) {
            getWorkflowFromJson {
                _selectScreenType.postValue(it.screens?.selectScreenType!!)
                nextScreenId = BasicFunction.getScreens()[_selectScreenType.value!!.nextScreen] as Int
                previousScreenId = BasicFunction.getScreens()[_selectScreenType.value!!.previousScreen] as Int
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
                _tv_select_screen_header.postValue(_selectScreenType.value!!.views?.textView?.header?.text!!)

            }
        }
    }
}