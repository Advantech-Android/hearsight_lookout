package com.adv.ilook.view.base

import android.app.ActivityManager
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.viewbinding.ViewBinding
import com.adv.ilook.model.util.permissions.Permission
import com.adv.ilook.model.util.permissions.PermissionManager
import com.google.android.material.snackbar.Snackbar

abstract class BaseFragment<VB:ViewBinding>:Fragment() {
    @Suppress("UNCHECKED_CAST")
    protected val binding: VB
        get() = _binding as VB
    private var _binding: ViewBinding? = null
    lateinit var observer: BaseLifecycleObserver
    val sharedModel: BaseViewModel by activityViewModels()
    private val _sharedModel get() = sharedModel
    private lateinit var navController: NavController
    protected lateinit var navHostFragment: NavHostFragment
    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB
    lateinit var localeUpdatedContext: ContextWrapper
     abstract fun setup(savedInstanceState: Bundle?)

     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         observer = BaseLifecycleObserver()
         lifecycle.addObserver(observer)
     }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bindingInflater.invoke(inflater, container, false)
        return requireNotNull(_binding).root
    }
     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
         super.onViewCreated(view, savedInstanceState)
         setup(savedInstanceState)
     }
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
                 if (granted) {
                     success("YES! Now I can access Storage and Location!")
                 } else {
                     error("Still missing at least one permission :(")
                 }
             }
     }


    private fun success( message: String="") {
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
         _binding = null
     }
     override fun onDestroy() {
         super.onDestroy()
         lifecycle.removeObserver(observer)
     }
}