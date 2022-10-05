package com.esoftwere.hfk.ui.notification_preference

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.esoftwere.hfk.R
import com.esoftwere.hfk.databinding.ActivityNotificationPreferenceBinding
import com.esoftwere.hfk.ui.notification_preference.adapter.ViewPagerControllerAdapter

class NotificationPreferenceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationPreferenceBinding
    private lateinit var mContext: Context
    private lateinit var mViewPagerControllerAdapter: ViewPagerControllerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification_preference)

        initToolbar()
        initVariable()
        initTab()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        NotificationPreferenceActivateFragment.mIsNotificationCatStatePrefUpdated = false
    }

    private fun initToolbar() {
        binding.toolbarCommon.apply {
            setSupportActionBar(toolbar)
            tvToolbarTitle.text = getString(R.string.label_notification_preference)
            ivToolbarBack?.setOnClickListener {
                toolbarBackClickHandler()
            }
        }
    }

    private fun initVariable() {
        mContext = this@NotificationPreferenceActivity
    }

    private fun initTab() {
        mViewPagerControllerAdapter = ViewPagerControllerAdapter(this@NotificationPreferenceActivity, supportFragmentManager)
        binding.vpNotificationPreferenceHolder.adapter = mViewPagerControllerAdapter
        binding.tabNotificationPermitController.setupWithViewPager(binding.vpNotificationPreferenceHolder)
    }

    /**
     * Click Handler
     */
    private fun toolbarBackClickHandler() {
        onBackPressed()
    }
}