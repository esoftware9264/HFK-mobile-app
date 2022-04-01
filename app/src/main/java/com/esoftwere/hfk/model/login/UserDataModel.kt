package com.esoftwere.hfk.model.login

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserDataModel(
    @field:SerializedName("id")
    val userId: String,
    @field:SerializedName("first_name")
    val firstName: String,
    @field:SerializedName("last_name")
    val lastName: String,
    @field:SerializedName("mobile")
    val mobile: String,
    @field:SerializedName("user_type")
    val userType: String,
    @field:SerializedName("state_name")
    val stateName: String,
    @field:SerializedName("country_id")
    val countryId: String,
    @field:SerializedName("state_id")
    val stateId: String,
    @field:SerializedName("district_name")
    val districtName: String,
    @field:SerializedName("district_id")
    val districtId: String,
    @field:SerializedName("block_name")
    val blockName: String,
    @field:SerializedName("block_id")
    val blockId: String,
    @field:SerializedName("landmark")
    val landmark: String,
    @field:SerializedName("village")
    val village: String,
    @field:SerializedName("isValidOtp")
    val isMobileNoVerified: Boolean,
    @field:SerializedName("active_flag")
    val activeFlag: String
) : Parcelable