package com.esoftwere.hfk.model.verify_otp

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VerifyOtpResponseModel(
    @field:SerializedName("success")
    val success: Boolean = false,
    @field:SerializedName("message")
    val message: String
) : Parcelable
