package com.adv.ilook.view.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.adv.ilook.R
import com.adv.ilook.model.util.responsehelper.UiStatus
import com.google.android.material.snackbar.Snackbar
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private const val TAG = "==>>BaseActivity"
abstract class BaseActivity <VB:ViewBinding> : AppCompatActivity(){
    @Suppress("UNCHECKED_CAST")
    protected val binding: VB
        get() = _binding as VB
    private var _binding: ViewBinding? = null
    private val sharedModel by viewModels<BaseViewModel>()
    val _sharedModel get() = sharedModel
    abstract val bindingInflater:(LayoutInflater)->VB
    abstract fun setup(savedInstanceState: Bundle?)
    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = bindingInflater.invoke(layoutInflater)
        setContentView(requireNotNull(_binding).root)
        createNavControl()
        setup(savedInstanceState)
    }
    private fun createNavControl() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.findNavController()
    }
    protected fun findNavHostFragment() = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment

    fun nav(id: Int) {
        navController.navigate(id)
    }
    fun changeStartDestination(fragmentId: Int) {
        val graph: NavGraph = navGraph()
        graph.id = fragmentId
        navHostFragment.navController.graph = graph
    }
    fun addDestination(fragmentId: Int){
        val graph: NavGraph = navGraph()
        navHostFragment.navController.graph=graph
    }
    private fun navGraph(): NavGraph {
        val inflater = navHostFragment.navController.navInflater
        return inflater.inflate(R.navigation.nav_design)
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    fun View.showSnackBar(view: View,
                          msg: String,
                          length: Int,
                          actionMessage: CharSequence?,
                          action: (View) -> Unit){
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
        if (status is UiStatus.Error) {
            scope.launch {
                showSnackbar(
                    message = status.data,
                    withDismissAction = true
                )
            }
        } else {
            this.currentSnackbarData?.dismiss()
        }
    }
    fun AppCompatActivity.getCameraAndMicPermission(view: View,success:()->Unit){
        PermissionX.init(this)
            .permissions(android.Manifest.permission.CAMERA,android.Manifest.permission.RECORD_AUDIO)
            .request{allGranted,_,_ ->
                if (allGranted){
                    success()
                } else{
                    Toast.makeText(this, "camera and mic permission is required", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }
}