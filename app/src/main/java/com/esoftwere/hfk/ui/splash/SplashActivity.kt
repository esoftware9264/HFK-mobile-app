package com.esoftwere.hfk.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.databinding.DataBindingUtil
import com.esoftwere.hfk.R
import com.esoftwere.hfk.core.AppConstants
import com.esoftwere.hfk.databinding.ActivitySplashBinding
import com.esoftwere.hfk.ui.home.HomeActivity
import com.esoftwere.hfk.ui.login.LoginActivity
import com.esoftwere.hfk.utils.AndroidUtility

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)

        splashTransitionHandler()
    }

    private fun splashTransitionHandler() {
        val splashHandler = Handler()
        splashHandler.postDelayed({
            val isUserLoggedIn = AndroidUtility.getIsUserLoggedIn()

            if (isUserLoggedIn) {
                moveToHomeActivity()
            } else {
                moveToLoginActivity()
            }
        }, AppConstants.MILLISECONDS_2000)
    }

    private fun moveToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }

    private fun moveToHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }
}