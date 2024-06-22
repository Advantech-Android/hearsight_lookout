package com.adv.ilook.view.base

import android.Manifest.permission.MANAGE_EXTERNAL_STORAGE
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
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
import com.adv.ilook.model.db.remote.repository.service.MainServiceActions
import com.adv.ilook.model.db.remote.repository.service.MainServiceRepository
import com.adv.ilook.model.util.extension.REQUEST_CODE_ALL
import com.adv.ilook.model.util.extension.REQUEST_CODE_BACKGROUND_LOCATION
import com.adv.ilook.model.util.extension.REQUEST_CODE_BLUETOOTH
import com.adv.ilook.model.util.extension.REQUEST_CODE_LOCATION
import com.adv.ilook.model.util.extension.REQUEST_CODE_MICROPHONE
import com.adv.ilook.model.util.extension.REQUEST_CODE_USB
import com.adv.ilook.model.util.extension.requestUsbPermission
import com.adv.ilook.model.util.responsehelper.UiStatus
import com.google.android.material.snackbar.Snackbar
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "==>>BaseActivity"

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity(), PermissionListener {
    @Suppress("UNCHECKED_CAST")
    protected val binding: VB
        get() = _binding as VB
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

    fun SnackbarHostState.showSnackBar(scope: CoroutineScope, status: UiStatus) {
        if (status is UiStatus.LoginFormState) {
            scope.launch {
                showSnackbar(
                    message = getString(status.message!!),
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
            if (allGranted) {
                sharedModel.actionLiveData.postValue("REQUEST_CODE_BLUETOOTH-TRUE")
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

            REQUEST_CODE_MICROPHONE -> {
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
                    Log.d(TAG, "onRequestPermissionsResult: Background location permission granted")
                    sharedModel.actionLiveData.postValue("REQUEST_CODE_BACKGROUND_LOCATION-TRUE")
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
        }
    }
}