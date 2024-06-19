package com.adv.ilook.view.base


import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adv.ilook.R
import com.adv.ilook.model.data.workflow.Workflow

import com.adv.ilook.model.util.assets.IPref
import com.adv.ilook.model.util.assets.SharedPrefKey.APP_ADV_WORKFLOW
import com.adv.ilook.model.util.network.NetworkHelper
import com.adv.ilook.view.ui.splash.TypeOfData
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale
import javax.inject.Inject

private const val TAG = "BaseViewModel"

@HiltViewModel
open class BaseViewModel @Inject constructor(private val networkHelper: NetworkHelper) :
    ViewModel() {
    val _speechCompletedLiveData = MutableLiveData<String>()
    var speechCompletedLiveData: LiveData<String> = _speechCompletedLiveData
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
                    launch(Dispatchers.IO) { getWorkflowPojo(jsonString) {} }
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
    private var locale: Int = 0
    private lateinit var localeSpeech: Locale
    private lateinit var mediaPlayer: MediaPlayer
    private var currentSongIndex = 0

    private val button_tones = listOf(
        R.raw.button_metallic_sound,
        R.raw.phone_key_sound,
        R.raw.notification_sound
    )

    fun initializeMediaPlayer(context :Context, currentSongIndex: Int){
       this.currentSongIndex=currentSongIndex
        mediaPlayer = MediaPlayer.create(context, button_tones[currentSongIndex])
        mediaPlayer.setOnCompletionListener {

            mediaPlayer.reset()
            initializeMediaPlayer(context, this.currentSongIndex)
            mediaPlayer.start()
        }
    }

    private fun playSong() {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }
    }
    private fun stopSong() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.reset()
        }
        mediaPlayer.release()
    }


    fun sendTextForSpeech(tts: TextToSpeech,
        text: String,
        volume: Float = 0.9f,
        pan: Float = 0.0f,
        stream: Float = 1.0f,
        pitch: Float = 1.0f,
        rate: Float = 1.1f,
        language: String = "en",
        country: String = "US"
    ) {
        localeSpeech = Locale(language, country)
        locale = if (tts.isLanguageAvailable(localeSpeech) >= TextToSpeech.LANG_AVAILABLE) {
            tts.setLanguage(localeSpeech)
        }else{
            tts.setLanguage(Locale.US)
        }
        val params = Bundle().apply {
            putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, volume) // Set volume (0.0 to 1.0)
            putFloat(TextToSpeech.Engine.KEY_PARAM_PAN, pan) // Set pan (-1.0 to 1.0)
            putFloat(TextToSpeech.Engine.KEY_PARAM_STREAM, stream)
        }
        tts.setPitch(pitch) // Set pitch (default is 1.0)
        tts.setSpeechRate(rate) // Set speech rate (default is 1.0)
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, params, "")

    }
    fun onInitSpeech(status:Int){
        if (status == TextToSpeech.SUCCESS) {
            if (locale == TextToSpeech.LANG_MISSING_DATA || locale == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e(TAG, "The Language not supported!")
               _speechCompletedLiveData.value = "failed"
            } else {
                _speechCompletedLiveData.value = "completed"
            }
        }
    }
    fun onDestroySpeech(tts: TextToSpeech){
            tts.stop()
            tts.shutdown()
    }
}