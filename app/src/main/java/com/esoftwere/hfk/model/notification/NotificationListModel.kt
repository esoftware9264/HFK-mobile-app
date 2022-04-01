package com.esoftwere.hfk.model.notification

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NotificationListModel(
    @field:SerializedName("id")
    val notificationId: String,
    @field:SerializedName("type_of_notification")
    val notificationType: String,
    @field:SerializedName("title")
    val notificationTitle: String,
    @field:SerializedName("created_date")
    val notificationDate: String
) : Parcelable
