package com.adv.ilook.view.base

import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.CAPTURE_AUDIO_OUTPUT
import android.Manifest.permission.FOREGROUND_SERVICE_MEDIA_PROJECTION
import android.Manifest.permission.MANAGE_EXTERNAL_STORAGE
import android.Manifest.permission.POST_NOTIFICATIONS
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.SnackbarHostState
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.adv.ilook.R
import com.adv.ilook.model.db.remote.repository.service.MainServiceRepository
import com.adv.ilook.model.util.extension.REQUEST_CODE_ALL
import com.adv.ilook.model.util.extension.REQUEST_CODE_BACKGROUND_LOCATION
import com.adv.ilook.model.util.extension.REQUEST_CODE_BLUETOOTH
import com.adv.ilook.model.util.extension.REQUEST_CODE_CAMERA_MICROPHONE
import com.adv.ilook.model.util.extension.REQUEST_CODE_LOCATION
import com.adv.ilook.model.util.extension.REQUEST_CODE_NOTIFICATION
import com.adv.ilook.model.util.extension.REQUEST_CODE_SCREEN_CAPTURE
import com.adv.ilook.model.util.extension.REQUEST_CODE_USB
import com.adv.ilook.model.util.extension.requestBackgroundLocationPermission
import com.adv.ilook.model.util.extension.requestCameraMicrophonePermission
import com.adv.ilook.model.util.extension.requestNotificationPermission
import com.adv.ilook.model.util.extension.requestUsbPermission

import com.adv.ilook.model.util.responsehelper.UiStatusLogin
import com.google.android.material.snackbar.Snackbar
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "==>>BaseActivity"

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity(), PermissionListener {
    @Suppress("UNCHECKED_CAST")
    protected val binding: VB get() = _binding as VB
    private var _binding: ViewBinding? = null
    private val sharedModel by viewModels<BaseViewModel>()
    val _sharedModel get() = sharedModel
    abstract val bindingInflater: (LayoutInflater) -> VB
    abstract fun setup(savedInstanceState: Bundle?)
    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment

    @Inject
    lateinit var mainServiceRepository: MainServiceRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ")
        _binding = bindingInflater.invoke(layoutInflater)

        configureWorkflow()
        setContentView(requireNotNull(_binding).root)
        createNavControl()
        setup(savedInstanceState)

    }



    private fun createNavControl() {
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()
    }

    protected fun findNavHostFragment() =
        supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment

    fun nav(id: Int) {
        navController.navigate(id)
    }

    private fun configureWorkflow() {
        lifecycleScope.launch { _sharedModel.configureWorkflow(applicationContext) }

    }

    fun changeStartDestination(fragmentId: Int) {
        val graph: NavGraph = navGraph()
        graph.id = fragmentId
        navHostFragment.navController.graph = graph
    }

    fun addDestination(fragmentId: Int) {
        val graph: NavGraph = navGraph()
        navHostFragment.navController.graph = graph
    }

    private fun navGraph(): NavGraph {
        val inflater = navHostFragment.navController.navInflater
        return inflater.inflate(R.navigation.nav_design)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    fun View.showSnackBar(
        view: View,
        msg: String,
        length: Int,
        actionMessage: CharSequence?,
        action: (View) -> Unit
    ) {
        val snackbar = Snackbar.make(view, msg, length)
        if (actionMessage != null) {

            snackbar.setAction(actionMessage) {
                action(this)
            }.show()
        } else {
            snackbar.show()
        }
    }

    fun SnackbarHostState.showSnackBar(scope: CoroutineScope, status: Any) {
        if (status is UiStatusLogin.LoginFormState) {
            scope.launch {
                showSnackbar(
                    message = getString(status.message!! as Int),
                    withDismissAction = true
                )
            }
        } else {
            this.currentSnackbarData?.dismiss()
        }
    }

    fun askPermission(
        view: ViewBinding,
        listOfPermission: ArrayList<String>,
        success: (Boolean) -> Unit
    ) {
        val permission = PermissionX.init(this)
        val permissionBuilder = permission.permissions(listOfPermission)
        permissionBuilder.request { allGranted, listOfAccepted, listOfDenied ->
            Log.d(
                TAG,
                "askPermission() called with: allGranted = $allGranted, listOfAccepted = $listOfAccepted, listOfDenied = $listOfDenied"
            )
            if (allGranted) {
                sharedModel.actionLiveData.postValue(listOfAccepted.toString())
                success(true)
            } else {
                listOfDenied.filter {
                    when (it) {
                        READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE, MANAGE_EXTERNAL_STORAGE -> {
                            requestUsbPermission(this) { result ->
                                if (result) {
                                    success(true)
                                } else {
                                    Log.d(

                                        TAG,
                                        "onRequestPermissionListener: USB permission denied, manually requested"
                                    )
                                    success(false)
                                }
                            }
                        }
                        POST_NOTIFICATIONS,
                        FOREGROUND_SERVICE_MEDIA_PROJECTION,
                        CAPTURE_AUDIO_OUTPUT->{
                            requestNotificationPermission(this) { result ->
                                if (result) {
                                    success(true)
                                } else {
                                    Log.d(

                                        TAG,
                                        "onRequestPermissionListener: Notification permission denied, manually requested"
                                    )
                                    success(false)
                                }
                            }
                        }
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.CAMERA,
                        Manifest.permission.FOREGROUND_SERVICE_CAMERA,
                        Manifest.permission.FOREGROUND_SERVICE_MICROPHONE ->{
                            requestCameraMicrophonePermission(this){ result ->
                                if (result) {
                                    success(true)
                                } else {
                                    Log.d(TAG,"onRequestPermissionListener: Camera and Microphone permission denied, manually requested" )
                                    success(false)
                                }
                            }
                        }
                        Manifest.permission.FOREGROUND_SERVICE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                        ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION ->{
                            requestBackgroundLocationPermission(this){ result ->
                                if (result) {
                                    success(true)
                                } else {
                                    Log.d(

                                        TAG,
                                        "onRequestPermissionListener:Location permission denied, manually requested"
                                    )
                                    success(false)
                                }
                            }
                        }

                        else -> {
                            _binding?.root?.apply {
                                showSnackBar(
                                    this.rootView,
                                    "Permission Denied",
                                    Snackbar.LENGTH_SHORT,
                                    "OK",
                                    {

                                    })
                            }

                        }
                    }
                    true
                }
            }
        }


    }

    var permissionInc = 0
    override fun onRequestPermissionListener(
        view: ViewBinding,
        listOfPermission: ArrayList<String>,
        success: (Boolean) -> Unit
    ) {
        Log.d(TAG, "onRequestPermissionListener: ")
        askPermission(view, listOfPermission) {
            success(it)
        }
        /*     requestBluetoothPermission(this) {
                 if (it) {
                     Log.d(
                         TAG,
                         "onRequestPermissionsResult: Bluetooth permission granted ==> permissionInc : $permissionInc"
                     )
                 } else {
                     Log.d(
                         TAG,
                         "onRequestPermissionListener: Bluetooth permission denied, manually requested ==> permissionInc : $permissionInc"
                     )
                 }
             }*/

        /*   _binding?.let {
               getCameraAndMicPermission(it.root){
                   Log.d(TAG, "onRequestPermissionListener: -----------------")
               }
           }*/
        /*    requestBluetoothPermission(this) {
                if (it)
                    Log.d(TAG, "onRequestPermissionsResult: Bluetooth permission granted")
                else Log.d(
                    TAG,
                    "onRequestPermissionListener: Bluetooth permission denied, manually requested"
                )
            }
            requestUsbPermission(this) {
                if (it)
                    Log.d(TAG, "onRequestPermissionsResult: USB permission granted")
                else Log.d(
                    TAG,
                    "onRequestPermissionListener: USB permission denied, manually requested"
                )
            }
            requestCameraMicrophonePermission(this) {
                if (it)
                    Log.d(TAG, "onRequestPermissionsResult: Microphone or Camera permission granted")
                else Log.d(
                    TAG,
                    "onRequestPermissionListener: Either Camera or Microphone permission denied, manually requested"
                )
            }
            requestLocationPermission(this) { outer ->
                if (outer) {
                    Log.d(TAG, "onRequestPermissionsResult: Location permission granted")
                    requestBackgroundLocationPermission(this) { inner ->
                        if (inner) {
                            // Background location access granted, proceed with location tracking
                            Log.d(
                                TAG,
                                "onRequestPermissionsResult: Background location permission granted"
                            )
                        } else {
                            Log.d(
                                TAG,
                                "onRequestPermissionsResult: Background location permission denied"
                            )
                        }
                    }
                } else {
                    Log.d(
                        TAG,
                        "onRequestPermissionListener: Location permission denied, manually requested"
                    )
                }

            }

         */


        /*requestAllPermission(this){
            if (it){
                Log.d(TAG, "onRequestPermissionsResult: All permission granted")
            }else{
                Log.d(TAG, "onRequestPermissionListener: All permission denied, manually requested")
            }
        }*/
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_BLUETOOTH -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult: Bluetooth permission granted")
                    sharedModel.actionLiveData.postValue("REQUEST_CODE_BLUETOOTH-TRUE")
                } else {
                    sharedModel.actionLiveData.postValue("REQUEST_CODE_BLUETOOTH-FALSE")
                }
            }

            REQUEST_CODE_USB -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult: USB permission granted")
                    sharedModel.actionLiveData.postValue("REQUEST_CODE_USB-TRUE")

                } else {
                    Log.d(TAG, "onRequestPermissionsResult: USB permission denied")
                    sharedModel.actionLiveData.postValue("REQUEST_CODE_USB-FALSE")
                }
            }


            REQUEST_CODE_CAMERA_MICROPHONE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(
                        TAG,
                        "onRequestPermissionsResult: camera and microphone permission granted"
                    )
                    sharedModel.actionLiveData.postValue("REQUEST_CODE_MICROPHONE-TRUE")

                } else {
                    sharedModel.actionLiveData.postValue("REQUEST_CODE_MICROPHONE-FALSE")
                }
            }

            REQUEST_CODE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        if (ContextCompat.checkSelfPermission(
                                this,
                                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            Log.d(
                                TAG,
                                "onRequestPermissionsResult: Background location permission granted"
                            )
                            sharedModel.actionLiveData.postValue("REQUEST_CODE_LOCATION-TRUE")
                        } else {

                            Log.d(
                                TAG,
                                "onRequestPermissionsResult: Background location permission denied"
                            )
                            sharedModel.actionLiveData.postValue("REQUEST_CODE_LOCATION-FALSE")
                        }
                    }

                } else {
                    // Handle location permission denied case (inform user)
                    sharedModel.actionLiveData.postValue("REQUEST_CODE_USB-FALSE")
                }
            }

            REQUEST_CODE_BACKGROUND_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        if (ContextCompat.checkSelfPermission(
                                this,
                                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            Log.d(
                                TAG,
                                "onRequestPermissionsResult: Background location permission granted"
                            )
                            sharedModel.actionLiveData.postValue("REQUEST_CODE_BACKGROUND_LOCATION-TRUE")
                        } else {

                            Log.d(
                                TAG,
                                "onRequestPermissionsResult: Background location permission denied"
                            )
                            sharedModel.actionLiveData.postValue("REQUEST_CODE_BACKGROUND_LOCATION-FALSE")
                        }
                    }
                } else {
                    sharedModel.actionLiveData.postValue("REQUEST_CODE_BACKGROUND_LOCATION-FALSE")
                }
            }

            REQUEST_CODE_ALL -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult: All permission granted")
                    sharedModel.actionLiveData.postValue("REQUEST_CODE_ALL-TRUE")
                } else {
                    Log.d(TAG, "onRequestPermissionsResult: All permission denied")
                    sharedModel.actionLiveData.postValue("REQUEST_CODE_ALL-FALSE")
                }
            }


            REQUEST_CODE_NOTIFICATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult: REQUEST_CODE_NOTIFICATION ==> All permission granted")
                    sharedModel.actionLiveData.postValue("REQUEST_CODE_NOTIFICATION-TRUE")
                } else {
                    Log.d(TAG, "onRequestPermissionsResult: REQUEST_CODE_NOTIFICATION ==> All permission denied")
                    sharedModel.actionLiveData.postValue("REQUEST_CODE_NOTIFICATION-FALSE")
                }
            }
        }
    }


}