package com.esoftwere.hfk.model.product_details

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductDetailsResponseModel(
    @field:SerializedName("success")
    val success: Boolean = false,
    @field:SerializedName("message")
    val message: String,
    @field:SerializedName("product_list")
    val productDetailsModel: ProductDetailsModel,
    @field:SerializedName("user_details")
    val userDetailsModel: UserDetailsModel
) : Parcelable
