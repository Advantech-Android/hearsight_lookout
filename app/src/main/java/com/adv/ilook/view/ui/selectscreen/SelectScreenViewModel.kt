package com.adv.ilook.view.ui.selectscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.adv.ilook.model.data.workflow.LoginScreen
import com.adv.ilook.model.data.workflow.SelectScreenType
import com.adv.ilook.model.db.remote.repository.CommonRepository
import com.adv.ilook.model.util.network.NetworkHelper
import com.adv.ilook.view.base.BaseViewModel
import com.adv.ilook.view.base.BasicFunction
import com.adv.ilook.view.ui.splash.TypeOfData
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.properties.Delegates

@HiltViewModel

class SelectScreenViewModel @Inject
constructor(
    private val loginRepository: CommonRepository,
    private val networkHelper: NetworkHelper
): BaseViewModel(networkHelper) {
    //private lateinit var selectScreenType: SelectScreenType
    private var selectScreenType by Delegates.notNull<SelectScreenType>()

    private val _nextScreenLiveData_1 = MutableLiveData<Int>()
    var nextScreenLiveData_1: LiveData<Int> = _nextScreenLiveData_1
    private val _nextScreenLiveData_2 = MutableLiveData<Int>()
    var nextScreenLiveData_2: LiveData<Int> = _nextScreenLiveData_2

    private val _prevScreenLiveData = MutableLiveData<Int>()
    var prevScreenLiveData: LiveData<Int> = _prevScreenLiveData

    private var nextScreenId_1 by Delegates.notNull<Int>()
    private var nextScreenId_2 by Delegates.notNull<Int>()
    private var previousScreenId by Delegates.notNull<Int>()



    override suspend fun init(function: (TypeOfData) -> Unit) {
        withContext(Dispatchers.IO) {
            getWorkflowFromJson {
                selectScreenType=it.screens?.selectScreenType!!
                nextScreenId_1 = BasicFunction.getScreens()[getNextScreen(0)] as Int
                nextScreenId_2 = BasicFunction.getScreens()[getNextScreen(1)] as Int
                previousScreenId = BasicFunction.getScreens()[selectScreenType.previousScreen] as Int
                _nextScreenLiveData_1.postValue(nextScreenId_1)
                _nextScreenLiveData_2.postValue(nextScreenId_2)
                _prevScreenLiveData.postValue(previousScreenId)

            }
            launch {
                _tv_select_screen_header.postValue(selectScreenType.views?.textView?.header?.text!!)
                _btn_guide_text.postValue(selectScreenType.views?.buttonView?.guideBtn?.text!!)
                _btn_vi_text.postValue(selectScreenType.views?.buttonView?.viBtn?.text!!)
            }
        }
    }

    private fun getNextScreen(index:Int) =
        if (selectScreenType.nextScreen == "null") selectScreenType.selectScreen?.get(index)?.nextScreen
        else selectScreenType.nextScreen

    private val _tv_select_screen_header=MutableLiveData<String>()
    var tv_select_screen_header:LiveData<String> = _tv_select_screen_header

    private val _btn_guide_text=MutableLiveData<String>()
    var btn_guide_text:LiveData<String> = _btn_guide_text
    private val _btn_vi_text=MutableLiveData<String>()
    var btn_vi_text:LiveData<String> = _btn_vi_text
}