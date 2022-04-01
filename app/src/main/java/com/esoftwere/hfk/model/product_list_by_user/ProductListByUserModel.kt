package com.esoftwere.hfk.model.product_list_by_user

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductListByUserModel(
    @field:SerializedName("id")
    val productId: String,
    @field:SerializedName("type")
    val productType: String,
    @field:SerializedName("product_name")
    val productName:String,
    @field:SerializedName("product_description")
    val productDescription: String,
    @field:SerializedName("price")
    val productPrice: String,
    @field:SerializedName("price_unit_id")
    val productPriceUnitId: String,
    @field:SerializedName("product_quantity")
    val productQuantity: String,
    @field:SerializedName("quantity_unit_id")
    val productQuantityUnitId: String,
    @field:SerializedName("image")
    val productImageUrl: String,
    @field:SerializedName("average_rating")
    val itemRating: String?
) : Parcelable
