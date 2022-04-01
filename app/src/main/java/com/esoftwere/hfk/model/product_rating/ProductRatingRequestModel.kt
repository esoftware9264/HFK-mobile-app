package com.esoftwere.hfk.model.product_rating

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductRatingRequestModel(
    @field:SerializedName("product_mst_id")
    val productMstId: String,
    @field:SerializedName("user_mst_id")
    val userMstId: String,
    @field:SerializedName("rating")
    val productRating: String,
    @field:SerializedName("note")
    val productComments: String
) : Parcelable