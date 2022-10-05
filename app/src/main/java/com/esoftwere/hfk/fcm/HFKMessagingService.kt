package com.esoftwere.hfk.fcm

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.esoftwere.hfk.R
import com.esoftwere.hfk.core.AppConstants
import com.esoftwere.hfk.core.HFKApplication
import com.esoftwere.hfk.ui.home.HomeActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class HFKMessagingService : FirebaseMessagingService() {
    private val TAG = "FCMMessagingService"

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Log.e(TAG, "OnTokenReceived:: $token")
        saveTokenToPreference(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.e(TAG, "OnMessageReceived:: $remoteMessage")
        remoteMessage.notification?.let { remoteNotification ->
            showNotification(remoteNotification.title, remoteNotification.body)
        }
    }

    private fun saveTokenToPreference(fcmToken: String) {
        HFKApplication.applicationInstance.tinyDB.writeString(AppConstants.KEY_PREFS_FCM_TOKEN, fcmToken)
    }

    private fun showNotification(title: String?, body: String?) {
        val notificationIntent = Intent(this, HomeActivity::class.java)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val pendingIntent =  if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_MUTABLE)
        } else {
             PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT)
        }

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notificationBuilder.build())
    }
}