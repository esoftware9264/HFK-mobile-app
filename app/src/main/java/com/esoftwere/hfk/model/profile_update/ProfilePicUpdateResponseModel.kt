package com.esoftwere.hfk.model.profile_update

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProfilePicUpdateResponseModel(
    @field:SerializedName("error")
    val error: Boolean,
    @field:SerializedName("success")
    val success: Boolean,
    @field:SerializedName("message")
    val message: String
) : Parcelable
