package com.esoftwere.hfk.model.edit_product

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EditProductRequestModel(
    @field:SerializedName("type")
    val type: String,
    @field:SerializedName("product_id")
    val productId: String,
    @field:SerializedName("quantity")
    val quantity: String,
    @field:SerializedName("quantity_unit_id")
    val quantityUnitId: String,
    @field:SerializedName("price")
    val price: String,
    @field:SerializedName("price_unit_id")
    val priceUnitId: String,
    @field:SerializedName("quality")
    val quality: String,
    @field:SerializedName("additional_info")
    val additionalInfo: String
) : Parcelable
