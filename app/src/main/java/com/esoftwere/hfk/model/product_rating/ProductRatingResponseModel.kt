package com.esoftwere.hfk.model.product_rating

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductRatingResponseModel(
    @field:SerializedName("success")
    val success: Boolean,
    @field:SerializedName("message")
    val message: String
) : Parcelable
