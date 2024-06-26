package com.adv.ilook.view.base

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorInt
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.adv.ilook.R
import com.adv.ilook.databinding.FragmentLoginBinding
import com.adv.ilook.databinding.FragmentSplashBinding
import com.adv.ilook.model.util.extension.requestBluetoothPermission
import com.adv.ilook.model.util.permissions.Permission
import com.adv.ilook.model.util.permissions.PermissionManager
import com.google.android.material.snackbar.Snackbar
import com.permissionx.guolindev.request.PermissionBuilder
import java.util.Locale
import kotlin.properties.Delegates


private const val TAG = "==>>BaseFragment"

abstract class BaseFragment<VB : ViewBinding> : Fragment(), TextToSpeech.OnInitListener {



    private lateinit var tts: TextToSpeech

    @Suppress("UNCHECKED_CAST")
    protected val binding: VB
        get():VB = _binding as VB
    private var _binding: ViewBinding? = null
    lateinit var observer: BaseLifecycleObserver
    val sharedModel: BaseViewModel by activityViewModels()
    private val _sharedModel get() = sharedModel
    private lateinit var navController: NavController
    protected lateinit var navHostFragment: NavHostFragment
    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB
    lateinit var localeUpdatedContext: ContextWrapper
    lateinit var activityListener: PermissionListener
    protected open var nextScreenId_1 by Delegates.notNull<Int>()
    protected open var nextScreenId_2 by Delegates.notNull<Int>()
    protected open var previousScreenId by Delegates.notNull<Int>()
    abstract fun setup(savedInstanceState: Bundle?)
    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "onAttach: ")
        activityListener = context as PermissionListener
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ")
        observer = BaseLifecycleObserver()
        lifecycle.addObserver(observer)
        tts = TextToSpeech(context, this)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: ")
        createNavControl()
        _binding = bindingInflater.invoke(inflater, container, false)
        return requireNotNull(_binding).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ")
        setup(savedInstanceState)
        // requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
    }

    fun createNavControl() {
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
    }

    fun nav(id: Int, bundle: Bundle = Bundle.EMPTY) {
        Log.d(TAG, "nav: $id")
       //(requireActivity() as NavigationHost).findNavControl()?.navigate(id, bundle)

        try {
            id.let { navController.navigate(it, bundle) }
        }catch (e:Exception){
            Log.d(TAG, "nav: ${e.message}")
        }

       
    }

    protected fun findNavControl() =
        (requireActivity() as NavigationHost).findNavControl()

    protected fun hideNavigation(animate: Boolean = true) =
        (requireActivity() as NavigationHost).hideNavigation(animate)

    protected fun showNavigation(animate: Boolean = true) =
        (requireActivity() as NavigationHost).showNavigation(animate)

    fun View.showSnackbar(
        view: View,
        msg: String,
        length: Int,
        actionMessage: CharSequence?,
        action: (View) -> Unit,
        action2: (View) -> Unit
    ) {
        val snackbar = Snackbar.make(view, msg, length)
        if (actionMessage != null) {
            snackbar.setAction(actionMessage) {
                action2(this)
            }.show()
        } else {
            snackbar.show()
        }
        action(this)
    }

    open fun showMessage(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun getPermissionManager(): PermissionManager {
        return PermissionManager.from(this)
    }

    fun clearAllPermissions() {
        val manager = requireContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        manager.clearApplicationUserData()
    }

    fun checkHSPermissions() {
        getPermissionManager().request(Permission.HS_Permissions)
            .rationale("We require to demonstrate that we can request two permissions at once")
            .checkPermission { granted ->
                Log.d(TAG, "checkHSPermissions: $granted")
                if (granted) {
                    success("YES! Now I can access Storage and Location!")
                } else {
                    error("Still missing at least one permission :(")
                }
            }
    }


    private fun success(message: String = "") {
        Snackbar.make(
            requireActivity().findViewById(android.R.id.content),
            message,
            Snackbar.LENGTH_SHORT
        )
            .withColor(Color.parseColor("#09AF00"))
            .show()
    }

    private fun error(message: String) {
        Snackbar.make(
            requireActivity().findViewById(android.R.id.content),
            message,
            Snackbar.LENGTH_SHORT
        )
            .withColor(Color.parseColor("#B00020"))
            .show()
    }

    private fun Snackbar.withColor(@ColorInt colorInt: Int): Snackbar {
        this.view.setBackgroundColor(colorInt)
        return this
    }


    override fun onDestroyView() {
        super.onDestroyView()
        lifecycle.removeObserver(observer)
        callback.remove()
        _binding = null
    }

    // Create an OnBackPressedCallback to handle the back button event
    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            isEnabled = true
            findNavControl()?.run {

            }
        }
    }

    /**speech from text content*/

    fun sendTextForSpeech(
        text: String,
        volume: Float = 0.9f,
        pan: Float = 0.0f,
        stream: Float = 1.0f,
        pitch: Float = 1.0f,
        rate: Float = 1.1f,
        language: String = "en",
        country: String = "US"
    ) {
        sharedModel.sendTextForSpeech(tts, text, volume, pan, stream, pitch, rate, language, country)
     }

    override fun onInit(status: Int) {
      sharedModel.onInitSpeech(status)
    }

    override fun onDestroy() {
        super.onDestroy()
      if (::tts.isInitialized)
          sharedModel.onDestroySpeech(tts)
    }
}