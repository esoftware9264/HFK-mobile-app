package com.esoftwere.hfk.model.home

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HomeProductItemModel(
    @field:SerializedName("id")
    val itemId: String,
    @field:SerializedName("type")
    val itemType: String,
    @field:SerializedName("product_status")
    val itemStatus: String,
    @field:SerializedName("image")
    val itemImageUrl: String?,
    @field:SerializedName("product_name")
    val itemName: String,
    @field:SerializedName("product_title")
    val itemTitle: String,
    @field:SerializedName("product_location")
    val productLocation: String,
    @field:SerializedName("price")
    val itemPrice: String,
    @field:SerializedName("price_unit_id")
    val itemPriceUnitId: String,
    @field:SerializedName("product_quantity")
    val itemQuantity: String,
    @field:SerializedName("quantity_unit_id")
    val itemQuantityUnitId: String,
    @field:SerializedName("average_rating")
    val itemRating: String,
    @field:SerializedName("category_id")
    val categoryId: String
) : Parcelable
