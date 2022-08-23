package com.esoftwere.hfk.model.market_view

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MarketViewItemModel(
    @field:SerializedName("id")
    val marketViewId: String,
    @field:SerializedName("category_name")
    val categoryName: String,
    @field:SerializedName("product_name")
    val productName: String,
    @field:SerializedName("type")
    val productType: String,
    @field:SerializedName("place")
    val productPlace: String,
    @field:SerializedName("state")
    val productState: String,
    @field:SerializedName("date")
    val productPriceUpdatedDate: String,
    @field:SerializedName("price")
    val productPrice: String
) : Parcelable
