package com.esoftwere.hfk.model.market_view

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MarketViewResponseModel(
    @field:SerializedName("success")
    val success: Boolean = false,
    @field:SerializedName("message")
    val message: String,
    @field:SerializedName("market_value_list")
    val marketViewItemList: ArrayList<MarketViewItemModel>
) : Parcelable
