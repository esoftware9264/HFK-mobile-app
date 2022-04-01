package com.esoftwere.hfk.model.seller_profile

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SellerProfileDataModel(
    @field:SerializedName("id")
    val sellerId: String,
    @field:SerializedName("first_name")
    val sellerFirstName: String,
    @field:SerializedName("last_name")
    val sellerLastName: String,
    @field:SerializedName("mobile")
    val sellerMobile: String,
    @field:SerializedName("email")
    val sellerEmail: String?,
    @field:SerializedName("state_name")
    val sellerStateName: String,
    @field:SerializedName("district_name")
    val sellerDistrictName: String,
    @field:SerializedName("block_name")
    val sellerBlockName: String,
    @field:SerializedName("location")
    val sellerLocation: String,
    @field:SerializedName("profile_pic")
    val sellerProfilePic: String
) : Parcelable
