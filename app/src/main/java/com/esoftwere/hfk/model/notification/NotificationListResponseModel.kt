package com.esoftwere.hfk.model.notification

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NotificationListResponseModel(
    @field:SerializedName("success")
    val success: Boolean = false,
    @field:SerializedName("message")
    val message: String,
    @field:SerializedName("notificationList")
    val notificationList: ArrayList<NotificationListModel>
) : Parcelable
