package com.esoftwere.hfk.model.search

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SearchItemModel(
    @field:SerializedName("id")
    val itemId: String,
    @field:SerializedName("name")
    val itemName: String,
    @field:SerializedName("image")
    val itemImage: String,
    @field:SerializedName("type")
    val itemType: String,
    @field:SerializedName("price")
    val itemPrice: String,
    @field:SerializedName("price_unit_id")
    val itemPriceUnitId: String,
    @field:SerializedName("product_quantity")
    val itemQuantity: String,
    @field:SerializedName("quantity_unit_id")
    val itemQuantityUnitId: String,
    @field:SerializedName("product_quality")
    val productQuality: String,
    @field:SerializedName("average_rating")
    val itemRating: String?
) : Parcelable
