package com.esoftwere.hfk.model.seller_profile

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SellerProfileRequestModel(
    @field:SerializedName("user_id") val userId: String
) : Parcelable