package com.esoftwere.hfk.model.notification_preference

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NotificationActiveCatListByUserRequestModel(
    @field:SerializedName("user_id")
    val userId: String
): Parcelable
