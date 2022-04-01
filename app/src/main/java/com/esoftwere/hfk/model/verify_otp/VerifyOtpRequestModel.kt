package com.esoftwere.hfk.model.verify_otp

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VerifyOtpRequestModel(
    @field:SerializedName("mobile_no")
    val mobileNo: String,
    @field:SerializedName("otp")
    val otp: String
): Parcelable
