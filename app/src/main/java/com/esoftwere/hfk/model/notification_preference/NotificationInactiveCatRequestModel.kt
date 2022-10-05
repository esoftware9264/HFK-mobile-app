package com.esoftwere.hfk.model.notification_preference

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NotificationInactiveCatRequestModel(
    @field:SerializedName("user_id")
    val userId: String,
    @field:SerializedName("cat_id")
    val inactiveCatIdList: ArrayList<String>
) : Parcelable
