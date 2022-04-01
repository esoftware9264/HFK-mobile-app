package com.esoftwere.hfk.model.fcm_token

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UpdateFCMTokenRequestModel(
    @field:SerializedName("id")
    val userId: String,
    @field:SerializedName("token")
    val fcmToken: String
) : Parcelable
