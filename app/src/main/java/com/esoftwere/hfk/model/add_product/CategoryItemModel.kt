package com.esoftwere.hfk.model.add_product

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CategoryItemModel(
    @field:SerializedName("id")
    val categoryId: String,
    @field:SerializedName("main_category_id")
    val mainCategoryID: String,
    @field:SerializedName("category_name")
    val categoryName: String,
    @field:SerializedName("category_image")
    val categoryImageUrl: String,
    @field:SerializedName("type")
    val categoryType: String = "",
    @field:SerializedName("active_flag")
    val activeFlag: String = "1"
) : Parcelable
