package com.esoftwere.hfk.model.login

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LoginRequestModel(
    @field:SerializedName("mobile")
    val mobile: String,
    @field:SerializedName("password")
    val password: String,
    @field:SerializedName("user_type")
    val userType: String
) : Parcelable
