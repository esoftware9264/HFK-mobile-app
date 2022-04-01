package com.esoftwere.hfk.ui.base

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.esoftwere.hfk.R

open class BaseActivity : AppCompatActivity() {
    private val TAG: String = "BaseActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun startFragment(fragment_container: Int, fragment: Fragment, clearBackStack: Boolean) {
        doStartFragment(fragment_container, fragment, clearBackStack)
    }

    fun startFragment(fragment_container: Int, fragment: Fragment) {
        doStartFragment(fragment_container, fragment, false)
    }

    private fun doStartFragment(
        fragment_container: Int,
        fragment: Fragment, clearBackStack: Boolean
    ) {
        if (clearBackStack) {
            clearBackStack()
            //showHideBack(false)
        } else {
            // showHideBack(true)
        }

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        //setCustomAnimations(int enter, int exit, int popEnter, int popExit)
        fragmentTransaction.setCustomAnimations(
            android.R.anim.fade_in,
            android.R.anim.fade_out,
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )

        fragmentTransaction.replace(fragment_container, fragment)
        //fragmentTransaction.addToBackStack(fragment.javaClass.name)
        Log.d(TAG, "FragmentName: " + fragment.javaClass.name)
        fragmentTransaction.commit()
        //autoShowHideBack()
    }

    private fun clearBackStack() {
        val manager = supportFragmentManager
        val count = manager.backStackEntryCount
        if (count > 0) {
            for (i in 0 until count) {
                manager.popBackStack()
            }
        }
    }
}