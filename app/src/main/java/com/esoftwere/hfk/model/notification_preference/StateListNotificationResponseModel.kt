package com.esoftwere.hfk.model.notification_preference

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StateListNotificationResponseModel(
    @field:SerializedName("success")
    val success: Boolean = false,
    @field:SerializedName("message")
    val message: String,
    @field:SerializedName("state_list")
    val stateList: ArrayList<MultiSelectStateModel>
) : Parcelable
