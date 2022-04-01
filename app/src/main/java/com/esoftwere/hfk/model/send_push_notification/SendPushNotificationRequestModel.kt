package com.esoftwere.hfk.model.send_push_notification

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SendPushNotificationRequestModel(
    @field:SerializedName("login_id")
    val loginId: String,
    @field:SerializedName("product_id")
    val productId: String
) : Parcelable
