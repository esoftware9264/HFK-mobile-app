package com.esoftwere.hfk.model.notification

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NotificationListRequestModel(
    @field:SerializedName("sender_id")
    val userId: String
) : Parcelable
