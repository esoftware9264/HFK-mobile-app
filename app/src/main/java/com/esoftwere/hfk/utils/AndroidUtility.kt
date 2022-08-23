package com.esoftwere.hfk.utils

import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.esoftwere.hfk.R
import com.esoftwere.hfk.core.AppConstants
import com.esoftwere.hfk.core.HFKApplication
import com.esoftwere.hfk.model.login.UserDataModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import java.util.regex.Pattern

object AndroidUtility {
    /**
     * @Task Dp to Px conversion
     */
    fun dpToPx(context: Context, dp: Int): Int {
        return (dp * (context.resources.displayMetrics.densityDpi / 160))
    }

    /**
     * @Task Px to Dp Conversion
     */
    fun pxToDp(context: Context, px: Int): Int {
        return (px / context.resources.displayMetrics.density).toInt()
    }

    /**
     * @Task Custom Error Toast
     */
    fun showErrorCustomToast(context: Context, message: String?) {
        val toast = Toast(context)
        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.layout_custom_error_toast, null)
        val textView = view.findViewById<TextView>(R.id.tv_errorText)
        textView.text = message
        toast.view = view
        toast.setGravity(Gravity.BOTTOM or Gravity.FILL_HORIZONTAL, 0, 0)
        toast.duration = Toast.LENGTH_SHORT
        toast.show()
    }

    /**
     * @Task Custom Error Toast
     */
    fun showSuccessCustomToast(context: Context, message: String?) {
        val toast = Toast(context)
        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.layout_custom_success_toast, null)
        val textView = view.findViewById<TextView>(R.id.tv_successText)
        textView.text = message
        toast.view = view
        toast.setGravity(Gravity.BOTTOM or Gravity.FILL_HORIZONTAL, 0, 0)
        toast.duration = Toast.LENGTH_SHORT
        toast.show()
    }

    /**
     * @Task Custom Error Snackbar
     */
    fun showErrorCustomSnackbar(container: View, message: String?) {
        val snack = Snackbar.make(container, ValidationHelper.optionalBlankText(message), Snackbar.LENGTH_LONG)

        snack.apply {
            view.setBackgroundColor(Color.parseColor("#FF0040"))
            setTextColor(Color.parseColor("#FFFFFF"))
            duration = Snackbar.LENGTH_SHORT
            show()
        }
    }

    /**
     * @Task Custom Error Toast
     */
    fun showSuccessCustomSnackbar(container: View, message: String?) {
        val snack = Snackbar.make(container, ValidationHelper.optionalBlankText(message), Snackbar.LENGTH_LONG)

        snack.apply {
            view.setBackgroundColor(Color.parseColor("#4CC552"))
            setTextColor(Color.parseColor("#FFFFFF"))
            duration = Snackbar.LENGTH_SHORT
            show()
        }
    }

    /**
     * @Task check if is Internet connection is available or not
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivity =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivity != null) {
            val info = connectivity.allNetworkInfo
            if (info != null) {
                for (i in info!!.indices) {
                    Log.w("INTERNET:", i.toString())
                    if (info!![i].state === NetworkInfo.State.CONNECTED) {
                        Log.w("INTERNET:", "connected!")
                        return true
                    }
                }
            }
        }
        return false
    }

    /**
     * @Task Print Model Class Data With The Help Of GSON
     */
    fun printModelDataWithGSON(tag: String = "ModelDataTag", modelData: Any?) {
        Log.e(tag, Gson().toJson(modelData))
    }

    /**
     * @Task color text
     */
    fun colorMyText(
        inputText: String,
        startIndex: Int,
        endIndex: Int,
        textColor: Int
    ): Spannable {
        val outPutColoredText: Spannable = SpannableString(inputText)
        outPutColoredText.setSpan(
            ForegroundColorSpan(textColor), startIndex, endIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return outPutColoredText
    }

    fun getAndroidDeviceId(context: Context) : String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    /**
     * Get User Details
     */
    fun getIsUserLoggedIn(): Boolean {
        return HFKApplication.applicationInstance.tinyDB.readBoolean(
            AppConstants.KEY_PREFS_USER_IS_LOGGED_IN,
            false
        )
    }

    fun getUserId(): String {
        return HFKApplication.applicationInstance.tinyDB.readString(
            AppConstants.KEY_PREFS_USER_ID,
            ""
        )
    }

    fun getUserImage(): String {
        return HFKApplication.applicationInstance.tinyDB.readString(
            AppConstants.KEY_PREFS_USER_IMAGE,
            ""
        )
    }

    fun getUserDetails(): UserDataModel? {
        return HFKApplication.applicationInstance.tinyDB.getCustomDataObjects<UserDataModel>(
            AppConstants.KEY_PREFS_USER_DETAILS
        )
    }

    fun getUserCountryId(): String {
        return ValidationHelper.optionalBlankText(getUserDetails()?.countryId)
    }

    fun getUserStateId(): String {
        return ValidationHelper.optionalBlankText(getUserDetails()?.stateId)
    }

    fun getUpdatedFCMToken(): String {
        return HFKApplication.applicationInstance.tinyDB.readString(
            AppConstants.KEY_PREFS_FCM_TOKEN,
            ""
        )
    }
}