package com.adv.ilook.view.ui

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.adv.ilook.R
import com.adv.ilook.databinding.ActivityMainBinding
import com.adv.ilook.view.base.BaseActivity
import com.adv.ilook.view.base.NavigationHost

private const val TAG = "==>>MainActivity"

class MainActivity:
    BaseActivity<ActivityMainBinding>(), NavigationHost {
    private var viewBinding: ActivityMainBinding? = null

     override val bindingInflater: (LayoutInflater) -> ActivityMainBinding
        get() = ActivityMainBinding::inflate

    override fun setup(savedInstanceState: Bundle?) {
        viewBinding = binding
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupBackPressed()
    }




    override fun findNavControl(): NavController?=findNavHostFragment()?.findNavController()

    override fun hideNavigation(animate: Boolean) {

    }

    override fun showNavigation(animate: Boolean) {

    }

    override fun openTab(navigationId: Int) {

    }

    override fun openDiscoverTab() {

    }
    private fun setupBackPressed() {
        val dispatcher=onBackPressedDispatcher
        dispatcher.addCallback(this)
        {
            isEnabled = false
            findNavControl()?.run {
                when(currentDestination?.id){

                }
            }
        }
    }
}