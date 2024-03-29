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
    @field:SerializedName("product_status")
    val productStatus: String,
    @field:SerializedName("product_description")
    val productDescription: String,
    @field:SerializedName("price")
    val productPrice: String,
    @field:SerializedName("priceUnitId")
    val priceUnitId: String,
    @field:SerializedName("price_unit_id")
    val priceUnitValue: String,
    @field:SerializedName("category_id")
    val categoryId: String,
    @field:SerializedName("image")
    val productImageList: ArrayList<String>,
    @field:SerializedName("product_quantity")
    val productQuantity: String,
    @field:SerializedName("quantityUnitId")
    val productQuantityUnitId: String,
    @field:SerializedName("quantity_unit_id")
    val productQuantityUnitValue: String,
    @field:SerializedName("product_quality")
    val productQuality: String,
    @field:SerializedName("created_at")
    val createdAt: String,
    @field:SerializedName("product_location")
    val productLocation: String,
    @field:SerializedName("machinery_years")
    val machineryYears: String,
    @field:SerializedName("machinery_months")
    val machineryMonths: String,
    @field:SerializedName("number_of_owners")
    val numberOfOwners: String,
    @field:SerializedName("product_view")
    val productViewCount: Int,
    @field:SerializedName("distance")
    val productDistance: String,
    @field:SerializedName("is_rating")
    val isRating: Int,
    @field:SerializedName("rating")
    val rating: String,
    @field:SerializedName("video_link")
    val videoLink: String
) : Parcelable
