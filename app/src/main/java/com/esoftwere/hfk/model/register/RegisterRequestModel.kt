package com.esoftwere.hfk.model.register

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RegisterRequestModel(
    @field:SerializedName("first_name")
    val firstName: String,
    @field:SerializedName("last_name")
    val lastName: String,
    @field:SerializedName("mobile")
    val mobile: String,
    @field:SerializedName("email")
    val email: String,
    @field:SerializedName("password")
    val password: String,
    @field:SerializedName("user_type")
    val userType: String,
    @field:SerializedName("country_id")
    val countryId: String,
    @field:SerializedName("state_id")
    val state: String,
    @field:SerializedName("district_id")
    val district: String,
    @field:SerializedName("block_id")
    val block: String,
    @field:SerializedName("pin_code")
    val pinCode: String,
    @field:SerializedName("device_id")
    val deviceId: String
) : Parcelable
