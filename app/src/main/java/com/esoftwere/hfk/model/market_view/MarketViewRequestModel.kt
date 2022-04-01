package com.esoftwere.hfk.model.market_view

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MarketViewRequestModel(
    @field:SerializedName("category_id")
    val categoryId: String,
    @field:SerializedName("product_id")
    val productId: String,
    @field:SerializedName("state_id")
    val stateId: String
): Parcelable
