package com.esoftwere.hfk.model.product_list_by_cat

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductListByCatResponseModel(
    @field:SerializedName("success")
    val success: Boolean = false,
    @field:SerializedName("message")
    val message: String,
    @field:SerializedName("product_list")
    val productListModel: ArrayList<ProductItemByCatModel>,
) : Parcelable
