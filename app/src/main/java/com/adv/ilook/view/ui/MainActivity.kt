package com.adv.ilook.view.ui

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.get
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.adv.ilook.R
import com.adv.ilook.databinding.ActivityMainBinding
import com.adv.ilook.model.db.remote.repository.service.MainServiceActions
import com.adv.ilook.view.base.BaseActivity
import com.adv.ilook.view.base.BaseViewModel
import com.adv.ilook.view.base.NavigationHost
import com.adv.ilook.view.ui.splash.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "==>>MainActivity"

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(), NavigationHost {
    private var viewBinding: ActivityMainBinding? = null

    override val bindingInflater: (LayoutInflater) -> ActivityMainBinding
        get() = ActivityMainBinding::inflate
    val sharedModel by viewModels<BaseViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (android.os.Build.VERSION.SDK_INT >= 34){
            onRequestPermissionListener(binding, arrayListOf(Manifest.permission.POST_NOTIFICATIONS,Manifest.permission.FOREGROUND_SERVICE_MEDIA_PROJECTION, "android.permission.CAPTURE_VIDEO_OUTPUT",
                "android.permission.PROJECT_MEDIA")) {
                startAppService()
            }
        }
        else
        {
            startAppService()
        }
    }
    override fun setup(savedInstanceState: Bundle?) {

        viewBinding = binding
        Log.d(TAG, "setup: ")
        setupBackPressed()
        sharedModel.actionLiveData.observe(this) {
            Log.d(TAG, "setup: $it")
            // nav(R.id.action_loginFragment_to_mainActivity2)
        }


    }
    private fun startAppService() {
        mainServiceRepository.startService( MainServiceActions.INIT_SERVICE.name)
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