package com.esoftwere.hfk.model.forgot_password

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ForgotPasswordResponseModel(
    @field:SerializedName("success")
    val success: Boolean = false,
    @field:SerializedName("message")
    val message: String
) : Parcelable
