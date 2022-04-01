package com.esoftwere.hfk.model.home

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HomeCategoryModel(
    @field:SerializedName("id")
    val categoryId: Int,
    @field:SerializedName("category_name")
    val categoryName: String,
    @field:SerializedName("category_image")
    val categoryImageUrl: String,
    @field:SerializedName("type")
    val categoryType: String,
    @field:SerializedName("active_flag")
    val activeFlag: String
) : Parcelable
