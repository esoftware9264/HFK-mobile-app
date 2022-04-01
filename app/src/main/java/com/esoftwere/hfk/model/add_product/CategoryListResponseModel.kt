package com.esoftwere.hfk.model.add_product

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CategoryListResponseModel(
    @field:SerializedName("success")
    val success: Boolean = false,
    @field:SerializedName("message")
    val message: String,
    @field:SerializedName("category_list")
    val categoryList: ArrayList<CategoryItemModel>
) : Parcelable
