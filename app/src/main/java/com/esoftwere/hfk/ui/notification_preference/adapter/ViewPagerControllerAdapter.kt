package com.esoftwere.hfk.ui.notification_preference.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.esoftwere.hfk.R
import com.esoftwere.hfk.ui.notification_preference.NotificationPreferenceActivateFragment
import com.esoftwere.hfk.ui.notification_preference.NotificationPreferenceDeactivateFragment

/**
 * @Author MSantanu
 * @Description VisitorPermitControllerAdapter class used to display
 * Upcoming and History Tab
 */
class ViewPagerControllerAdapter(context: Context, fm: FragmentManager) : FragmentPagerAdapter(fm) {
    private val mContext: Context

    init {
        mContext = context
    }

    override fun getItem(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> {
                //Get Notification Active Fragment Instance
                fragment = NotificationPreferenceActivateFragment.newInstance()
            }
            1 -> {
                //Get Notification Deactive Fragment Instance
                fragment = NotificationPreferenceDeactivateFragment.newInstance()
            }
        }
        return fragment!!
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence {
        var title: String = ""
        when (position) {
            0 -> {
                //Set Upcoming Tab Title
                title = mContext.getString(R.string.label_activate)
            }
            1 -> {
                //Set History Tab Title
                title = mContext.getString(R.string.label_deactivate)
            }
        }
        return title
    }
}