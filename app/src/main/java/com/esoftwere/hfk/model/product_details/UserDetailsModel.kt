package com.esoftwere.hfk.model.product_details

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserDetailsModel(
    @field:SerializedName("id")
    val userId: String,
    @field:SerializedName("first_name")
    val firstName: String,
    @field:SerializedName("last_name")
    val lastName: String,
    @field:SerializedName("mobile")
    val mobile: String,
    @field:SerializedName("email")
    val email: String,
    @field:SerializedName("user_type")
    val userType: String,
    @field:SerializedName("village")
    val village: String,
    @field:SerializedName("block")
    val block: String,
    @field:SerializedName("landmark")
    val landmark: String,
) : Parcelable
