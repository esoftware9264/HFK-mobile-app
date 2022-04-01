package com.esoftwere.hfk.model.seller_profile

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SellerProfileResponseModel(
    @field:SerializedName("error")
    val error: Boolean,
    @field:SerializedName("success")
    val success: Boolean,
    @field:SerializedName("message")
    val message: String,
    @field:SerializedName("userData")
    val sellerProfileDataModel: SellerProfileDataModel
) : Parcelable
