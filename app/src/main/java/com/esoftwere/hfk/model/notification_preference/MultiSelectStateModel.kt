package com.esoftwere.hfk.model.notification_preference

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MultiSelectStateModel(
    @field:SerializedName("id")
    val stateId: String,
    @field:SerializedName("state")
    val state: String,
    @field:SerializedName("isSelected")
    var isItemSelected: Boolean = false,
    @field:SerializedName("active_flag")
    val activeFlag: String
) : Parcelable
