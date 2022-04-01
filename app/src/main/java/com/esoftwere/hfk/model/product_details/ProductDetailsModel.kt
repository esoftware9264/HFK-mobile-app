package com.esoftwere.hfk.model.product_details

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductDetailsModel(
    @field:SerializedName("id")
    val productId: String,
    @field:SerializedName("product_name")
    val productName: String,
    @field:SerializedName("product_title")
    val productTitle: String,
    @field:SerializedName("product_description")
    val productDescription: String,
    @field:SerializedName("price")
    val productPrice: String,
    @field:SerializedName("price_unit_id")
    val priceUnitId: String,
    @field:SerializedName("image")
    val productImageList: ArrayList<String>,
    @field:SerializedName("product_quantity")
    val productQuantity: String,
    @field:SerializedName("quantity_unit_id")
    val productQuantityUnitId: String,
    @field:SerializedName("product_quality")
    val productQuality: String,
    @field:SerializedName("created_at")
    val createdAt: String,
    @field:SerializedName("product_location")
    val productLocation: String,
    @field:SerializedName("is_rating")
    val isRating: Int,
    @field:SerializedName("rating")
    val rating: String,
    @field:SerializedName("video_link")
    val videoLink: String
) : Parcelable
