package com.esoftwere.hfk.model.product_details

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductDetailsRequestModel(
    @field:SerializedName("product_id")
    val productId: String,
    @field:SerializedName("type")
    val productType: String,
    @field:SerializedName("state_id")
    val userStateId: String,
    @field:SerializedName("user_mst_id")
    val userMstId: String
) : Parcelable
