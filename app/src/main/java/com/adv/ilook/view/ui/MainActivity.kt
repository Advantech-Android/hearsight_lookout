package com.adv.ilook.view.ui

import android.Manifest
import android.content.Intent
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.get
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.adv.ilook.R
import com.adv.ilook.databinding.ActivityMainBinding
import com.adv.ilook.model.db.remote.repository.service.MainService
import com.adv.ilook.model.db.remote.repository.service.MainServiceActions
import com.adv.ilook.model.util.extension.REQUEST_CODE_SCREEN_CAPTURE
import com.adv.ilook.view.base.BaseActivity
import com.adv.ilook.view.base.BaseViewModel
import com.adv.ilook.view.base.NavigationHost
import com.adv.ilook.view.ui.splash.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "==>>MainActivity"

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(), NavigationHost {
    private var viewBinding: ActivityMainBinding? = null
    private lateinit var mediaProjectionManager: MediaProjectionManager
    private var mediaProjection: MediaProjection? = null
    override val bindingInflater: (LayoutInflater) -> ActivityMainBinding
        get() = ActivityMainBinding::inflate
    val sharedModel by viewModels<BaseViewModel>()
    override fun setup(savedInstanceState: Bundle?) {
        viewBinding = binding
        setupBackPressed()
        if (android.os.Build.VERSION.SDK_INT >= 34) {
            Log.d(TAG, "setup: -------if")
            onRequestPermissionListener(
                binding,
                arrayListOf( Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.FOREGROUND_SERVICE_MEDIA_PROJECTION,
                    Manifest.permission.FOREGROUND_SERVICE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.CAMERA,
                    Manifest.permission.FOREGROUND_SERVICE_CAMERA,
                    Manifest.permission.FOREGROUND_SERVICE_MICROPHONE )
            ) { result ->
                if (result)
                    startAppService()
                else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Log.d(TAG, "setup: ----->else")
            startAppService()
        }


    }

    private fun startAppService(isMediaProjection: Boolean = false) {
        if (isMediaProjection) mainServiceRepository.startMediaProjectionService(MainServiceActions.HANDLE_PROJECTION.name)
        else mainServiceRepository.startService(MainServiceActions.INIT_SERVICE.name)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mediaProjectionManager =
            getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

        sharedModel.actionLiveData.observe(this) {
            Log.d(TAG, "onCreate: actionLiveData ==> ${it}")
            if (it == "REQUEST_CODE_NOTIFICATION-TRUE") startScreenCapture()
        }
    }

    private fun startScreenCapture() {
        val captureIntent = mediaProjectionManager.createScreenCaptureIntent()
        startActivityForResult(captureIntent, REQUEST_CODE_SCREEN_CAPTURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_SCREEN_CAPTURE) {
            if (resultCode == RESULT_OK && data != null) {
                mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data)
                startAppService(isMediaProjection = true)
            } else {
                Toast.makeText(this, "Screen capture permission denied", Toast.LENGTH_SHORT).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }



    override fun findNavControl(): NavController? = findNavHostFragment()?.findNavController()

    override fun hideNavigation(animate: Boolean) {
        Log.d(TAG, "hideNavigation: ")
    }

    override fun showNavigation(animate: Boolean) {
        Log.d(TAG, "showNavigation: ")
    }

    override fun openTab(navigationId: Int) {
        Log.d(TAG, "openTab: ")
    }

    override fun openDiscoverTab() {
        Log.d(TAG, "openDiscoverTab: ")
    }


    override fun getOnBackInvokedDispatcher(): OnBackInvokedDispatcher {
        return super.getOnBackInvokedDispatcher()
    }

    private fun setupBackPressed() {
        Log.d(TAG, "setupBackPressed: ")
        onBackPressedDispatcher.addCallback(this, callback)
    }

    // Create an OnBackPressedCallback to handle the back button event
    val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            // Handle the back button event here
            mainServiceRepository.stopService()
            isEnabled = false
            findNavControl()?.run {
                when (currentDestination?.id) {
                    R.id.splashFragment -> {
                        Log.d(TAG, "handleOnBackPressed: splash")
                    }

                    R.id.loginFragment -> {
                        Log.d(TAG, "handleOnBackPressed: login")
                    }

                    else -> {

                    }
                }
            }
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: ")
        super.onDestroy()
        callback.remove()
        viewBinding = null
    }
}