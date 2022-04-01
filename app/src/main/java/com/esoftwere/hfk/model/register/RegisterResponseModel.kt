package com.esoftwere.hfk.model.register

import android.os.Parcelable
import com.esoftwere.hfk.model.login.UserDataModel
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RegisterResponseModel(
    @field:SerializedName("success")
    val success: Boolean = false,
    @field:SerializedName("message")
    val message: String,
    @field:SerializedName("isOtp")
    val isOtp: Boolean = false,
    @field:SerializedName("userDetails")
    val userDataModel: UserDataModel
) : Parcelable
