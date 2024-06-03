package com.adv.ilook.view.ui.splash

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navOptions
import coil.Coil
import coil.util.CoilUtils
import com.adv.ilook.R
import com.adv.ilook.databinding.FragmentSplashBinding
import com.adv.ilook.model.util.assets.SharedPrefKey
import com.adv.ilook.model.util.permissions.PermissionManager
import com.adv.ilook.view.base.BaseFragment
import com.adv.ilook.view.base.BasicFunction
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import kotlin.properties.Delegates

private const val TAG = "==>>SplashFragment"

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding>() {


    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSplashBinding
        get() = FragmentSplashBinding::inflate
    private var _viewBinding: FragmentSplashBinding? = null
    val viewModel by viewModels<SplashViewModel>()
    private var nextScreenId by Delegates.notNull<Int>()
    private var previousScreenId by Delegates.notNull<Int>()
    private val handler = Handler(Looper.getMainLooper())
    override fun setup(savedInstanceState: Bundle?) {
        Log.d(TAG, "setup: ")
        _viewBinding = binding
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.init { }
        }
    }
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
        _viewBinding?.apply {

            /* headMotionLayout.postDelayed(Runnable {

                 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                     handler.postDelayed(runnableInner,2000)
                 }
             }, 1000)
 */

            // Glide.with(requireActivity()).asGif().load("res/drawable/e.gif").into(backIView)
        }
        viewModel.nextScreenLiveData.observe(viewLifecycleOwner) {
            nav(it)
        }
    }

    val runnableInner = {
        nav(R.id.action_splashFragment_to_loginFragment)
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "onAttach: ")

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "onDetach: ")
    }



    override fun onDestroyView() {
        Log.d(TAG, "onDestroyView: ")
        super.onDestroyView()
        // handler.removeCallbacks(runnableInner)
        _viewBinding = null
    }
}