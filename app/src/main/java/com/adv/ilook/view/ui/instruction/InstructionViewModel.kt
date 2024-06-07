package com.adv.ilook.view.ui.instruction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adv.ilook.model.data.workflow.InstructionScreen
import com.adv.ilook.model.data.workflow.SelectScreenType
import com.adv.ilook.model.db.remote.repository.CommonRepository
import com.adv.ilook.model.util.network.NetworkHelper
import com.adv.ilook.view.base.BaseViewModel
import com.adv.ilook.view.base.BasicFunction
import com.adv.ilook.view.ui.splash.TypeOfData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.properties.Delegates

@HiltViewModel
class InstructionViewModel @Inject
constructor(
    private val loginRepository: CommonRepository,
    private val networkHelper: NetworkHelper
) : BaseViewModel(networkHelper) {
    //private lateinit var instructionScreen: InstructionScreen
    private var instructionScreen by Delegates.notNull<InstructionScreen>()

    private val _nextScreenLiveData_1 = MutableLiveData<Int>()
    var nextScreenLiveData_1: LiveData<Int> = _nextScreenLiveData_1
    private val _nextScreenLiveData_2 = MutableLiveData<Int>()
    var nextScreenLiveData_2: LiveData<Int> = _nextScreenLiveData_2

    private val _prevScreenLiveData = MutableLiveData<Int>()
    var prevScreenLiveData: LiveData<Int> = _prevScreenLiveData


    override suspend fun init(function: (TypeOfData) -> Unit) {
        withContext(Dispatchers.IO) {
            getWorkflowFromJson {
                instructionScreen = it.screens?.instructionScreen!!
                _nextScreenLiveData_1.postValue(
                    BasicFunction.getScreens()[instructionScreen.selectScreen?.get(
                        0
                    )?.nextScreen] as Int
                )
                _prevScreenLiveData.postValue(
                    BasicFunction.getScreens()[instructionScreen.selectScreen?.get(
                        0
                    )?.previousScreen] as Int
                )
            }
            launch {
                _tv_select_screen_header.postValue(instructionScreen.views?.textView?.header?.text!!)
                _html_terms_of_use.postValue(instructionScreen.views?.textView?.termsOfUseText?.helperText!!)
                _html_terms_of_use_enable.postValue(instructionScreen.views?.textView?.termsOfUseText?.enable!!)
                _btn_agree_text.postValue(instructionScreen.views?.buttonView?.agreeBtn?.text!!)
                _btn_disagree_text.postValue(instructionScreen.views?.buttonView?.disagreeBtn?.text!!)
            }
        }
    }

    private val _tv_select_screen_header = MutableLiveData<String>()
    var tv_select_screen_header: LiveData<String> = _tv_select_screen_header
    private val _html_terms_of_use = MutableLiveData<String>()
    var html_terms_of_use: LiveData<String> = _html_terms_of_use
    private val _html_terms_of_use_enable = MutableLiveData<Boolean>()
    var html_terms_of_use_enable: LiveData<Boolean> = _html_terms_of_use_enable
    private val _btn_agree_text = MutableLiveData<String>()
    var btn_agree_text: LiveData<String> = _btn_agree_text
    private val _btn_disagree_text = MutableLiveData<String>()
    var btn_disagree_text: LiveData<String> = _btn_disagree_text

}

