package com.esoftwere.hfk.model.forgot_password

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ForgotPasswordRequestModel(
    @field:SerializedName("mobile")
    val mobileNo: String
) : Parcelable
