package com.adv.ilook.view.base


import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adv.ilook.model.data.workflow.Workflow

import com.adv.ilook.model.util.assets.IPref
import com.adv.ilook.model.util.assets.SharedPrefKey.APP_ADV_WORKFLOW
import com.adv.ilook.model.util.network.NetworkHelper
import com.adv.ilook.view.ui.splash.TypeOfData
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

private const val TAG = "BaseViewModel"

@HiltViewModel
open class BaseViewModel @Inject constructor(private val networkHelper: NetworkHelper) : ViewModel() {
    val actionLiveData = MutableLiveData<String>()
    @Inject
    lateinit var basicFunction: BasicFunction

    @Inject
    open lateinit var sharedPref: IPref

    @Inject
    lateinit var gson: Gson

    @Inject
    lateinit var jsonObj: JSONObject

    @Inject
    lateinit var jsonArray: JSONArray
    lateinit var owner: LifecycleOwner
    lateinit var workflow: Workflow
    private var jsonString: String = ""
    var jsonStringFromAsset: String = ""
    open fun callOtherActivity(activity: Activity, msg: String) {

    }

    open suspend fun init(function: (TypeOfData) -> Unit) {}


    suspend fun configureWorkflow(
        context: Context,
        fileName: String = "workflow.json",
        key: String = APP_ADV_WORKFLOW
    ) {
        withContext(Dispatchers.IO) {
            jsonString = basicFunction.getFileFromAsset(fileName, context)
            sharedPref.put(key = key, jsonString)
            when (key) {
                APP_ADV_WORKFLOW -> {
                   launch(Dispatchers.IO) {  getWorkflowPojo(jsonString) {} }
                }

                else -> {
                    // getPojoFromStrJson(jsonString, key) {}
                }
            }
        }


    }

    suspend fun getWorkflowFromJson(
        jsonStr: String = "",
        key: String = APP_ADV_WORKFLOW,
        function: (Workflow) -> Unit
    ) {
        var tempString = ""
        if (jsonStr.isNotEmpty()) {
            tempString = jsonStr
        } else {
            jsonString = sharedPref.str(key)
            tempString = jsonString
            Log.i(TAG, "getWorkflowFromJson:  $jsonString")

        }
        withContext(Dispatchers.IO) {
            getWorkflowPojo(tempString) {
                workflow = it
                function(it)
            }
        }
    }

    private suspend fun getWorkflowPojo(jsonString: String, function: (Workflow) -> Unit) {
        workflow = gson.fromJson(jsonString, Workflow::class.java)
        function(workflow)
    }
}